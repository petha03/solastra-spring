<template>
  <div class="file-upload">
    <div class="upload-area" @dragover.prevent @drop.prevent="handleDrop">
      <input
        type="file"
        ref="fileInput"
        @change="handleFileSelect"
        :multiple="multiple"
        :accept="accept"
        class="file-input"
      />
      <div class="upload-content" @click="triggerFileSelect">
        <div v-if="!files.length">
          <p class="upload-icon">📁</p>
          <p class="upload-text">Click to select or drag and drop files here</p>
        </div>
        <div v-else class="file-list">
          <div v-for="(file, index) in files" :key="index" class="file-item">
            <span class="file-name">{{ file.name }}</span>
            <span class="file-size">{{ formatFileSize(file.size) }}</span>
            <button @click.stop="removeFile(index)" class="remove-btn">×</button>
          </div>
        </div>
      </div>
    </div>
    <button v-if="files.length" @click="uploadFiles" class="upload-btn" :disabled="uploading">
      {{ uploading ? 'Uploading...' : 'Upload Files' }}
    </button>
  </div>
</template>

<script>
export default {
  name: 'FileUpload',
  props: {
    multiple: {
      type: Boolean,
      default: true
    },
    accept: {
      type: String,
      default: ''
    },
    uploadUrl: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      files: [],
      uploading: false
    }
  },
  methods: {
    triggerFileSelect() {
      this.$refs.fileInput.click()
    },
    handleFileSelect(event) {
      const selectedFiles = Array.from(event.target.files)
      this.addFiles(selectedFiles)
    },
    handleDrop(event) {
      const droppedFiles = Array.from(event.dataTransfer.files)
      this.addFiles(droppedFiles)
    },
    addFiles(newFiles) {
      if (this.multiple) {
        this.files = [...this.files, ...newFiles]
      } else {
        this.files = newFiles.slice(0, 1)
      }
      this.$emit('files-selected', this.files)
    },
    removeFile(index) {
      this.files.splice(index, 1)
      this.$emit('files-selected', this.files)
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 Bytes'
      const k = 1024
      const sizes = ['Bytes', 'KB', 'MB', 'GB']
      const i = Math.floor(Math.log(bytes) / Math.log(k))
      return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i]
    },
    async uploadFiles() {
      if (!this.files.length) return

      this.uploading = true
      const formData = new FormData()

      // For single file upload, use 'file' parameter
      // For multiple files, use 'files' parameter
      if (this.files.length === 1) {
        console.log('Uploading single file:', this.files[0])
        console.log('File name:', this.files[0].name, 'Size:', this.files[0].size)
        formData.append('file', this.files[0])
      } else {
        console.log('Uploading multiple files:', this.files.length)
        this.files.forEach((file) => {
          console.log('File:', file.name, 'Size:', file.size)
          formData.append('files', file)
        })
      }

      console.log('Upload URL:', this.uploadUrl)
      console.log('FormData entries:', Array.from(formData.entries()).map(([key, value]) => ({
        key,
        fileName: value instanceof File ? value.name : 'not a file',
        size: value instanceof File ? value.size : 'n/a'
      })))

      try {
        const response = await fetch(this.uploadUrl, {
          method: 'POST',
          body: formData
        })

        console.log('Response status:', response.status)
        console.log('Response headers:', Object.fromEntries(response.headers.entries()))

        if (response.ok) {
          const result = await response.json()
          console.log('Upload success:', result)
          this.$emit('upload-success', result)
          this.files = []
          this.$refs.fileInput.value = ''
        } else {
          const errorData = await response.json().catch(() => ({ error: 'Upload failed' }))
          console.error('Upload error response:', errorData)
          throw new Error(errorData.error || 'Upload failed')
        }
      } catch (error) {
        console.error('Upload exception:', error)
        this.$emit('upload-error', error)
      } finally {
        this.uploading = false
      }
    }
  }
}
</script>

<style scoped>
.file-upload {
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
}

.upload-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 40px;
  background-color: #f9f9f9;
  transition: all 0.3s ease;
}

.upload-area:hover {
  border-color: #42b983;
  background-color: #f0f9f4;
}

.file-input {
  display: none;
}

.upload-content {
  cursor: pointer;
  text-align: center;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 10px;
}

.upload-text {
  color: #666;
  margin: 0;
}

.file-list {
  text-align: left;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: white;
  border-radius: 4px;
  margin-bottom: 8px;
}

.file-name {
  flex: 1;
  font-weight: 500;
}

.file-size {
  color: #666;
  margin: 0 10px;
  font-size: 0.9em;
}

.remove-btn {
  background: none;
  border: none;
  color: #ff4444;
  font-size: 24px;
  cursor: pointer;
  padding: 0 5px;
  line-height: 1;
}

.remove-btn:hover {
  color: #cc0000;
}

.upload-btn {
  margin-top: 20px;
  padding: 12px 30px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.upload-btn:hover:not(:disabled) {
  background-color: #359268;
}

.upload-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}
</style>