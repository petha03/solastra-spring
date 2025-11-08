import { useState } from 'react'
import FileUpload from './components/FileUpload'
import './App.css'

function App() {
  const [uploadedFiles, setUploadedFiles] = useState([])

  const handleFilesUploaded = (files) => {
    setUploadedFiles(prev => [...prev, ...files])
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>File Upload</h1>
        <p>Upload your files securely</p>
      </header>

      <main className="app-main">
        <FileUpload onFilesUploaded={handleFilesUploaded} />

        {uploadedFiles.length > 0 && (
          <div className="uploaded-files-section">
            <h2>Uploaded Files</h2>
            <ul className="uploaded-files-list">
              {uploadedFiles.map((file, index) => (
                <li key={index} className="uploaded-file-item">
                  <span className="file-name">{file.name}</span>
                  <span className="file-size">{formatFileSize(file.size)}</span>
                  <span className="file-status success">Uploaded</span>
                </li>
              ))}
            </ul>
          </div>
        )}
      </main>
    </div>
  )
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
}

export default App