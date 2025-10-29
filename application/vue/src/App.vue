<template>
  <div id="app">
    <header>
      <h1>Welcome to Vue</h1>
    </header>
    <main>
      <p>Start building your single page application here.</p>
      <FileUpload
        :multiple="true"
        :upload-url="apiUrl + '/upload/file'"
        @files-selected="handleFilesSelected"
        @upload-success="handleUploadSuccess"
        @upload-error="handleUploadError"
      />
      <div v-if="message" class="message" :class="messageType">
        {{ message }}
      </div>
    </main>
  </div>
</template>

<script>
import FileUpload from './components/FileUpload.vue'

export default {
  name: 'App',
  components: {
    FileUpload
  },
  data() {
    return {
      apiUrl: import.meta.env.VITE_API_BASE_URL || '',
      message: '',
      messageType: ''
    }
  },
  methods: {
    handleFilesSelected(files) {
      console.log('Files selected:', files)
    },
    handleUploadSuccess(result) {
      this.message = 'Files uploaded successfully!'
      this.messageType = 'success'
      console.log('Upload result:', result)
      setTimeout(() => {
        this.message = ''
      }, 3000)
    },
    handleUploadError(error) {
      this.message = 'Upload failed: ' + error.message
      this.messageType = 'error'
      console.error('Upload error:', error)
      setTimeout(() => {
        this.message = ''
      }, 3000)
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}

.message {
  margin-top: 20px;
  padding: 12px 20px;
  border-radius: 4px;
  font-weight: 500;
}

.message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}
</style>