#!/bin/bash

BUCKET_NAME="solastra-vue-app"
VUE_APP_DIR="/tmp/vue-app"

echo "Waiting for S3 bucket to be created by Terraform..."
sleep 5

echo "Checking if Vue app build exists..."
if [ -d "$VUE_APP_DIR" ] && [ "$(ls -A $VUE_APP_DIR)" ]; then
    echo "Uploading Vue app to S3 bucket: $BUCKET_NAME"
    awslocal s3 sync $VUE_APP_DIR s3://$BUCKET_NAME/ --delete
    echo "Vue app uploaded successfully!"
    echo "Access at: http://$BUCKET_NAME.s3-website.localhost.localstack.cloud:4566"
else
    echo "Vue app build directory is empty or doesn't exist at $VUE_APP_DIR"
    echo "Run 'npm run build' in application/vue first, then restart LocalStack"
fi