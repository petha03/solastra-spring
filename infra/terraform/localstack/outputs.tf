output "lambda_function_name" {
  value = aws_lambda_function.solastra_function.function_name
}

output "lambda_function_arn" {
  value = aws_lambda_function.solastra_function.arn
}

output "api_gateway_url" {
  value = "${aws_api_gateway_stage.dev.invoke_url}"
}

output "api_gateway_id" {
  value = aws_api_gateway_rest_api.solastra_api.id
}

output "localstack_api_url" {
  description = "LocalStack API Gateway URL accessible from browser"
  value = "http://localhost.localstack.cloud:4566/restapis/${aws_api_gateway_rest_api.solastra_api.id}/${aws_api_gateway_stage.dev.stage_name}/_user_request_"
}

output "vue_app_url" {
  value = "http://${aws_s3_bucket.vue_app.bucket}.s3-website.localhost.localstack.cloud:4566"
}

output "react_app_url" {
  value = "http://${aws_s3_bucket.react_app.bucket}.s3-website.localhost.localstack.cloud:4566"
}

output "launcher_url" {
  value = "http://${aws_s3_bucket.launcher.bucket}.s3-website.localhost.localstack.cloud:4566"
}

