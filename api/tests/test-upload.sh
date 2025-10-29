#!/bin/bash

# Get the API Gateway URL from environment or use default
API_ID=${1:-$(cd ../../../infra/terraform/localstack && terraform output -raw api_gateway_id 2>/dev/null)}
BASE_URL="http://localhost:4566/restapis/${API_ID}/dev/_user_request_"

echo "Testing file uploads to: $BASE_URL"
echo "=========================================="

# Test 1: Upload single file
echo ""
echo "Test 1: Upload single file"
curl -X POST "${BASE_URL}/upload/file" \
  -F "file=@./resources/sample-upload.txt" \
  -H "Accept: application/json" \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "=========================================="

# Test 2: Upload multiple files (if your controller supports it)
echo ""
echo "Test 2: Upload multiple files"
curl -X POST "${BASE_URL}/upload/files" \
  -F "files=@./resources/sample-upload.txt" \
  -F "files=@./resources/sample-upload.txt" \
  -H "Accept: application/json" \
  -w "\nHTTP Status: %{http_code}\n"

echo ""
echo "=========================================="
echo "Tests completed!"