import { useState } from 'react'
import { useAuth } from './contexts/AuthContext'
import Login from './components/auth/Login'
import Register from './components/auth/Register'
import FileUpload from './components/FileUpload'
import './App.css'

function App() {
  const [uploadedFiles, setUploadedFiles] = useState([])
  const [showRegister, setShowRegister] = useState(false)
  const { user, loading, logout } = useAuth()

  const handleFilesUploaded = (files) => {
    setUploadedFiles(prev => [...prev, ...files])
  }

  const handleSwitchToRegister = () => {
    setShowRegister(true)
  }

  const handleSwitchToLogin = () => {
    setShowRegister(false)
  }

  if (loading) {
    return (
      <div className="app">
        <div className="loading-screen">
          <h2>Loading...</h2>
        </div>
      </div>
    )
  }

  // Show login/register if not authenticated
  if (!user) {
    if (showRegister) {
      return <Register onSwitchToLogin={handleSwitchToLogin} />
    }
    return <Login onSwitchToRegister={handleSwitchToRegister} />
  }

  // Show file upload page if authenticated
  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <div>
            <h1>File Upload</h1>
            <p>Upload your files securely</p>
          </div>
          <div className="header-actions">
            <span className="user-email">{user.email}</span>
            <button onClick={logout} className="logout-button">
              Logout
            </button>
          </div>
        </div>
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