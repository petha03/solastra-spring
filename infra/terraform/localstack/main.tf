data "aws_iam_policy_document" "lambda_assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "lambda_execution_role" {
  name               = "solastra-lambda-execution-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume_role.json
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_execution_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_lambda_function" "solastra_function" {
  filename         = "${path.module}/../../../api/boot/build/distributions/solastra.zip"
  function_name    = "solastra-test"
  role            = aws_iam_role.lambda_execution_role.arn
  handler         = "com.solastra.adapters.in.lambda.StreamLambdaHandler::handleRequest"
  source_code_hash = filebase64sha256("${path.module}/../../../api/boot/build/distributions/solastra.zip")
  runtime         = "java21"
  timeout         = 30
  memory_size     = 512

  snap_start {
    apply_on = "PublishedVersions"
  }
}

resource "aws_api_gateway_rest_api" "solastra_api" {
  name        = "solastra-api"
  description = "Solastra API Gateway"
}

resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.solastra_api.id
  parent_id   = aws_api_gateway_rest_api.solastra_api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.solastra_api.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda" {
  rest_api_id = aws_api_gateway_rest_api.solastra_api.id
  resource_id = aws_api_gateway_method.proxy.resource_id
  http_method = aws_api_gateway_method.proxy.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.solastra_function.invoke_arn
}

# Enable CORS for OPTIONS requests
resource "aws_api_gateway_method" "options" {
  rest_api_id   = aws_api_gateway_rest_api.solastra_api.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "options" {
  rest_api_id = aws_api_gateway_rest_api.solastra_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.options.http_method
  type        = "MOCK"

  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_method_response" "options_200" {
  rest_api_id = aws_api_gateway_rest_api.solastra_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.options.http_method
  status_code = "200"

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true
    "method.response.header.Access-Control-Allow-Methods" = true
    "method.response.header.Access-Control-Allow-Origin"  = true
  }

  response_models = {
    "application/json" = "Empty"
  }
}

resource "aws_api_gateway_integration_response" "options_200" {
  rest_api_id = aws_api_gateway_rest_api.solastra_api.id
  resource_id = aws_api_gateway_resource.proxy.id
  http_method = aws_api_gateway_method.options.http_method
  status_code = aws_api_gateway_method_response.options_200.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Requested-With'"
    "method.response.header.Access-Control-Allow-Methods" = "'GET,OPTIONS,POST,PUT,DELETE'"
    "method.response.header.Access-Control-Allow-Origin"  = "'*'"
  }

  depends_on = [
    aws_api_gateway_integration.options
  ]
}

resource "aws_api_gateway_deployment" "deployment" {
  depends_on = [
    aws_api_gateway_integration.lambda,
    aws_api_gateway_integration.options
  ]

  rest_api_id = aws_api_gateway_rest_api.solastra_api.id

  triggers = {
    redeployment = sha1(jsonencode([
      aws_api_gateway_resource.proxy.id,
      aws_api_gateway_method.proxy.id,
      aws_api_gateway_integration.lambda.id,
      aws_api_gateway_method.options.id,
      aws_api_gateway_integration.options.id,
      aws_api_gateway_integration_response.options_200.id,
    ]))
  }

  lifecycle {
    create_before_destroy = true
  }
}

resource "aws_api_gateway_stage" "dev" {
  deployment_id = aws_api_gateway_deployment.deployment.id
  rest_api_id   = aws_api_gateway_rest_api.solastra_api.id
  stage_name    = "dev"
}

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.solastra_function.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_api_gateway_rest_api.solastra_api.execution_arn}/*/*"
}

# S3 bucket for Vue application
resource "aws_s3_bucket" "vue_app" {
  bucket = "solastra-vue-app"
}

# S3 bucket for file uploads
resource "aws_s3_bucket" "uploads" {
  bucket = "solastra-uploads"
}

resource "aws_s3_bucket_website_configuration" "vue_app" {
  bucket = aws_s3_bucket.vue_app.id

  index_document {
    suffix = "index.html"
  }

  error_document {
    key = "index.html"
  }
}

resource "aws_s3_bucket_public_access_block" "vue_app" {
  bucket = aws_s3_bucket.vue_app.id

  block_public_acls       = false
  block_public_policy     = false
  ignore_public_acls      = false
  restrict_public_buckets = false
}

resource "aws_s3_bucket_policy" "vue_app" {
  bucket = aws_s3_bucket.vue_app.id

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid       = "PublicReadGetObject"
        Effect    = "Allow"
        Principal = "*"
        Action    = "s3:GetObject"
        Resource  = "${aws_s3_bucket.vue_app.arn}/*"
      }
    ]
  })

  depends_on = [aws_s3_bucket_public_access_block.vue_app]
}

output "api_endpoint" {
  value = "${aws_api_gateway_stage.dev.invoke_url}"
}

output "vue_app_url" {
  value = "http://${aws_s3_bucket.vue_app.bucket}.s3-website.localhost.localstack.cloud:4566"
}
