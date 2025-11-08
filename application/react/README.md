# React File Upload Application

A modern, single-page React application for uploading files with drag-and-drop support.

## Features

- Drag and drop file upload interface
- Click to browse and select files
- Multiple file selection support
- File size display
- Visual feedback for drag operations
- Responsive design with modern UI
- Built with React 18 and Vite

## Project Structure

```
application/react/
├── src/
│   ├── components/
│   │   ├── FileUpload.jsx       # Main file upload component
│   │   └── FileUpload.css       # File upload component styles
│   ├── App.jsx                  # Root application component
│   ├── App.css                  # Application styles
│   ├── main.jsx                 # Application entry point
│   └── index.css                # Global styles
├── public/                      # Static assets
├── index.html                   # HTML entry point
├── vite.config.js              # Vite configuration
├── package.json                # Dependencies and scripts
└── build.gradle                # Gradle build configuration
```

## Development

### Install Dependencies

```bash
npm install
```

### Run Development Server

```bash
npm run local
```

The application will be available at `http://localhost:3001`

### Build for Production

```bash
npm run build              # Production build
npm run build:dev          # Development environment build
npm run build:uat          # UAT environment build
npm run build:localstack   # LocalStack environment build
```

## Gradle Tasks

The module integrates with the main Gradle build:

```bash
# Build the React application
./gradlew :application:react:build

# Install dependencies only
./gradlew :application:react:npmInstall

# Clean build artifacts
./gradlew :application:react:clean

# Build for LocalStack
./gradlew :application:react:buildLocal
```

## Component Usage

The `FileUpload` component accepts an `onFilesUploaded` callback:

```jsx
import FileUpload from './components/FileUpload'

function App() {
  const handleFilesUploaded = (files) => {
    console.log('Uploaded files:', files)
    // Handle file upload logic here
  }

  return <FileUpload onFilesUploaded={handleFilesUploaded} />
}
```

## Environment Variables

The application uses Vite environment variables:

- `VITE_API_BASE_URL` - Base URL for API endpoints

Environment-specific configuration files:
- `.env.local` - Local development
- `.env.localstack` - LocalStack environment (auto-generated)

## Technology Stack

- React 18.3.1
- Vite 6.0.0
- Modern CSS with gradients and animations