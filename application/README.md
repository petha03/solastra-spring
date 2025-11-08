# Solastra Applications

This directory contains the frontend applications for the Solastra project.

## Applications

### Vue Application (`vue/`)
Single-page Vue.js application with modern UI components.

- **Framework**: Vue 3.4.0
- **Build Tool**: Vite 6.0.0
- **Dev Server Port**: 3000
- **Location**: `application/vue/`

[View Vue App Documentation](./vue/README.md)

### React Application (`react/`)
File upload application with drag-and-drop support built with React.

- **Framework**: React 18.3.1
- **Build Tool**: Vite 6.0.0
- **Dev Server Port**: 3001
- **Location**: `application/react/`
- **Features**: File upload with drag-and-drop, S3 integration

[View React App Documentation](./react/README.md)

### Launcher (`launcher/`)
Landing page for switching between Vue and React applications in LocalStack.

- **Type**: Static HTML page
- **Purpose**: Application selector for development
- **Location**: `application/launcher/`

## Development

### Running Applications Locally

**Vue App:**
```bash
cd vue
npm install
npm run local
```
Access at: http://localhost:3000

**React App:**
```bash
cd react
npm install
npm run local
```
Access at: http://localhost:3001

### Building Applications

**Build both apps:**
```bash
./gradlew rebuildAppsForLocalStack
```

**Build individual apps:**
```bash
./gradlew :application:vue:buildLocal
./gradlew :application:react:buildLocal
```

## LocalStack Deployment

### Full Deployment
Deploy all applications to LocalStack:
```bash
./gradlew deployLocal
```

This will:
1. Build both Vue and React apps
2. Apply Terraform infrastructure
3. Upload apps to S3 buckets
4. Deploy the launcher page

### Access URLs (after deployment)
- **Launcher**: http://solastra-launcher.s3-website.localhost.localstack.cloud:4566
- **Vue App**: http://solastra-vue-app.s3-website.localhost.localstack.cloud:4566
- **React App**: http://solastra-react-app.s3-website.localhost.localstack.cloud:4566

## Environment Configuration

Both Vue and React apps use Vite environment variables:

**Environment Files:**
- `.env.local` - Local development
- `.env.localstack` - LocalStack (auto-generated during build)
- `.env.dev` - Development environment
- `.env.uat` - UAT environment
- `.env.prod` - Production environment

**Variables:**
- `VITE_API_BASE_URL` - Base URL for API endpoints

Environment files for LocalStack are automatically generated during deployment based on Terraform outputs.

## Build Scripts

All build scripts are defined in the root `build.gradle`:

| Task | Description |
|------|-------------|
| `rebuildVueForLocalStack` | Rebuild Vue app for LocalStack |
| `rebuildReactForLocalStack` | Rebuild React app for LocalStack |
| `rebuildAppsForLocalStack` | Rebuild both apps for LocalStack |
| `uploadVueToS3` | Upload Vue app to S3 |
| `uploadReactToS3` | Upload React app to S3 |
| `uploadLauncherToS3` | Upload launcher to S3 |
| `deployLocal` | Full LocalStack deployment |

## Architecture

Both applications are:
- Built with Vite for fast development and optimized production builds
- Deployed to S3 buckets configured for static website hosting
- Connected to the Spring Boot API via API Gateway
- Configured with CORS to allow cross-origin requests

### API Integration

Both apps communicate with the Spring Boot API deployed as AWS Lambda function behind API Gateway:

```
Frontend Apps (S3) → API Gateway → Lambda (Spring Boot) → Services/S3
```

## Project Structure

```
application/
├── vue/                    # Vue.js application
│   ├── src/               # Vue source code
│   ├── public/            # Static assets
│   ├── dist/              # Build output
│   └── package.json       # Dependencies
├── react/                 # React application
│   ├── src/               # React source code
│   ├── public/            # Static assets
│   ├── dist/              # Build output
│   └── package.json       # Dependencies
├── launcher/              # Static launcher page
│   └── index.html        # Application selector
└── README.md             # This file
```

## Common Issues

### CORS Errors
If you see CORS errors, ensure you're using the correct API URL with `localhost.localstack.cloud` domain (not just `localhost`).

### Files Not Uploading
1. Check that the API Gateway URL is correctly set in `.env.localstack`
2. Verify LocalStack is running: `docker ps | grep localstack`
3. Check API endpoint: `/upload/file` for single file uploads

### Build Errors
1. Ensure dependencies are installed: `npm install`
2. Check that Node.js version is compatible (Node 16+)
3. Clear build cache: `rm -rf dist node_modules && npm install`

## Additional Resources

- [Vite Documentation](https://vitejs.dev/)
- [Vue 3 Documentation](https://vuejs.org/)
- [React Documentation](https://react.dev/)
- [LocalStack Documentation](https://docs.localstack.cloud/)