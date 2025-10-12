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

