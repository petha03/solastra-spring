# Solastra Spring

Solastra Spring is a full-stack platform combining a Spring Boot serverless API with a Vue.js frontend. It's designed for AWS Lambda deployment with LocalStack support for local development and testing.

The architecture follows hexagonal design principles, separating business logic, application services, and infrastructure adapters. The API uses Spring Boot 3.5+ with WebFlux for reactive request handling and is optimized for AWS Lambda with SnapStart support. AWS Serverless Java Container bridges Spring Boot with Lambda's execution model, enabling standard Spring patterns in a serverless environment.

Infrastructure is managed as code using Terraform. LocalStack provides a local AWS environment for building and testing without connecting to AWS. The Gradle build system automates the workflow, including building components, deploying to LocalStack, and updating environment configurations.

The Vue.js frontend uses Vite 6.0 and supports multiple environment configurations (local, development, UAT, production). Features include single-command deployment, hot-reload development servers, and automated test configuration updates.

## Project Structure

```
solastra-spring/
├── api/boot/              # Spring Boot Lambda API
├── application/vue/       # Vue.js SPA frontend
├── infra/terraform/       # Infrastructure as Code
└── build.gradle          # Root Gradle build configuration
```

## Prerequisites

- **Java 21** (for the API)
- **Node.js 18+** and npm (for the Vue application)
- **Gradle** (wrapper included via `./gradlew`)
- **Docker** (for LocalStack)
- **Terraform** (for infrastructure deployment)

## Building the Project

### Build Everything

```bash
# Build both API and application
./gradlew build
```

### Build API Only

```bash
# Build the Spring Boot Lambda function
./gradlew :api:boot:shadowJar

# Build as ZIP for Lambda deployment
./gradlew :api:boot:buildZip
```

The API jar will be created at: `api/boot/build/libs/solastra.jar`

### Build Application Only

```bash
# Install dependencies
./gradlew :application:vue:npmInstall

# Build for production
./gradlew :application:vue:npmBuild

# Build for specific environments
./gradlew :application:vue:npmBuild -Penv=dev
./gradlew :application:vue:npmBuild -Penv=uat
./gradlew :application:vue:npmBuild -Penv=prod
```

The built files will be in: `application/vue/dist/`

## Running Locally

### Option 1: LocalStack (Full Stack)

This is the recommended way to run the entire stack locally with AWS services emulated.

```bash
# Deploy everything to LocalStack (starts Docker, builds, and deploys)
./gradlew deployLocal
```

This command will:
1. Build the API and application
2. Start LocalStack via Docker Compose
3. Apply Terraform configuration (creates Lambda and API Gateway)
4. Update Vue environment with API Gateway URL
5. Update HTTP client test environment with API Gateway ID
6. Rebuild Vue app with correct API endpoint
7. Upload Vue app to S3

After deployment:
- **Vue App**: http://solastra-vue-app.s3-website.localhost.localstack.cloud:4566
- **API Gateway**: The Vue app is automatically configured to use the LocalStack API Gateway endpoint
- **HTTP Client Tests**: `api/tests/http-client.env.json` is automatically updated with the API Gateway ID and base URL
- To view API Gateway URL: Run `cd infra/terraform/localstack && terraform output api_gateway_url`

### Option 2: Vue Development Server

For frontend development with hot-reload:

```bash
# Run from root directory
./gradlew runVue
```

The development server will run at `http://localhost:3972`.

Alternatively, you can run it directly from the Vue directory:

```bash
cd application/vue

# Install dependencies (first time only)
npm install

# Run development server connected to LocalStack
npm run local
```

### Individual LocalStack Tasks

```bash
# Recreate LocalStack from scratch
./gradlew recreateLocalStack

# Restart LocalStack containers
./gradlew restartLocalStack

# Apply Terraform changes only
./gradlew applyTerraForm
```

## Testing

### Run All Tests

```bash
./gradlew test
```

### Run API Tests Only

```bash
./gradlew :api:boot:test
```

### HTTP Client Tests

The project includes HTTP client tests in `api/tests/api-tests.http`. After running `./gradlew deployLocal`, the environment configuration is automatically updated in `api/tests/http-client.env.json` with:

```json
{
  "dev": {
    "apiId": "zqlxpzhja8",
    "baseUrl": "http://localhost:4566/restapis/zqlxpzhja8/dev"
  }
}
```

You can run these tests directly in IntelliJ IDEA or any IDE that supports `.http` files with the HTTP Client plugin.

## Cleaning Build Artifacts

```bash
# Clean everything
./gradlew clean

# Clean specific modules
./gradlew :api:boot:clean
./gradlew :application:vue:clean
```

## Architecture

### API (api/boot)

- Spring Boot 3.5.6 with AWS Lambda support
- Uses AWS Serverless Java Container for Spring Boot
- Webflux for lightweight dispatching (Tomcat excluded)
- SnapStart optimized for fast cold starts
- Built as an uber JAR using Shadow plugin

### Application (application/vue)

- Vue.js 3.4+ SPA
- Vite 6.0 for building and development
- Environment-specific builds (localstack, dev, uat, prod)
- API endpoint configured via environment variables

### Infrastructure

- Terraform for AWS resource management
- Docker Compose for LocalStack
- S3 for static hosting
- API Gateway for API routing
- Lambda for serverless compute

## Environment Variables

### Vue Application

The Vue application uses environment-specific configuration files:

- **`.env.localstack`**: Auto-generated during `./gradlew deployLocal` with the LocalStack API Gateway URL
- **`.env.local`**: For local development server (create manually if needed)
- **`.env.dev`**: Development environment configuration
- **`.env.uat`**: UAT environment configuration
- **`.env.prod`**: Production environment configuration (uses `vite build` without suffix)

The `.env.localstack` file is automatically created with the correct API Gateway endpoint when you run `./gradlew deployLocal`. It contains:

```
VITE_API_BASE_URL=https://{api-id}.execute-api.us-east-1.amazonaws.com/dev
```

**Note**: `.env.localstack` is auto-generated and should not be edited manually. It's excluded from git.

## Gradle Tasks Reference

| Task | Description |
|------|-------------|
| `build` | Build entire project |
| `runVue` | Run Vue dev server with hot-reload on port 3972 |
| `deployLocal` | Full LocalStack deployment (builds, deploys, uploads) |
| `uploadVueToS3` | Upload Vue app to LocalStack S3 |
| `recreateLocalStack` | Recreate LocalStack containers |
| `restartLocalStack` | Restart LocalStack |
| `applyTerraForm` | Apply Terraform configuration |
| `:api:boot:shadowJar` | Build API uber JAR |
| `:api:boot:buildZip` | Build API ZIP for Lambda |
| `:api:boot:buildLocal` | Build API for LocalStack |
| `:application:vue:npmInstall` | Install npm dependencies |
| `:application:vue:npmBuild` | Build Vue app |
| `:application:vue:buildLocal` | Build Vue app for LocalStack |

## Troubleshooting

### LocalStack Issues

If LocalStack isn't starting properly:

```bash
cd infra/terraform/localstack
docker compose down
docker compose up -d
```

### Build Issues

If you encounter build issues, try cleaning first:

```bash
./gradlew clean build
```

### Node Modules Issues

```bash
cd application/vue
rm -rf node_modules package-lock.json
npm install
```