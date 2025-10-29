# solastra-spring

A Spring Boot serverless API with a Vue.js single-page application frontend, designed for deployment on AWS Lambda with LocalStack support for local development.

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
3. Apply Terraform configuration
4. Deploy Lambda function and static assets to LocalStack

After deployment, the Vue app will be accessible via the LocalStack S3 endpoint, and API calls will go through the LocalStack API Gateway.

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

Create `application/vue/.env.local` for local development:

```
VITE_API_BASE_URL=http://localhost:4566/restapis/{api-id}/dev/_user_request_
```

This is automatically generated when running `./gradlew deployLocal`.

## Gradle Tasks Reference

| Task | Description |
|------|-------------|
| `build` | Build entire project |
| `:api:boot:shadowJar` | Build API uber JAR |
| `:api:boot:buildZip` | Build API ZIP for Lambda |
| `:api:boot:buildLocal` | Build API for LocalStack |
| `:application:vue:npmInstall` | Install npm dependencies |
| `:application:vue:npmBuild` | Build Vue app |
| `:application:vue:buildLocal` | Build Vue app for LocalStack |
| `deployLocal` | Full LocalStack deployment |
| `recreateLocalStack` | Recreate LocalStack containers |
| `restartLocalStack` | Restart LocalStack |
| `applyTerraForm` | Apply Terraform configuration |

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