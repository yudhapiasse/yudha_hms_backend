# Patient Photo Upload & Management API Documentation

## Overview

This document describes the comprehensive patient photo upload and management system implemented in the HMS backend. The system provides secure file upload, thumbnail generation, caching, and GDPR-compliant deletion capabilities.

## Features

### 1. **Photo Upload**
- Multipart file upload with validation
- File size limit: 5MB (configurable)
- Allowed formats: JPG, JPEG, PNG, GIF, WEBP
- Automatic old photo replacement

### 2. **Thumbnail Generation**
- Automatic thumbnail creation (150x150 pixels)
- High-quality image processing using Thumbnailator library
- Aspect ratio preservation
- Optimized for fast loading in lists and grids

### 3. **File Storage**
- Local filesystem storage with configurable paths
- Secure filename generation
- Path traversal prevention
- Organized directory structure

### 4. **Caching & Performance**
- HTTP caching headers (30 days)
- Public cache for CDN compatibility
- Optimized image serving
- Thumbnail support for quick loading

### 5. **GDPR Compliance**
- Complete photo deletion (original + thumbnail)
- Database record cleanup
- Right to erasure compliance
- Audit logging

### 6. **Default Avatar Fallback**
- Automatic fallback to default avatar
- Consistent UI experience
- No broken images

## Configuration

### Application Configuration (`application.yml`)

```yaml
hms:
  file-storage:
    upload-dir: ${HMS_UPLOAD_DIR:./uploads}
    patient-photos-dir: ${HMS_UPLOAD_DIR:./uploads}/patient-photos
    thumbnails-dir: ${HMS_UPLOAD_DIR:./uploads}/patient-photos/thumbnails
    max-file-size: 5242880 # 5MB in bytes
    allowed-extensions: jpg,jpeg,png,gif,webp
    thumbnail-width: 150
    thumbnail-height: 150
    default-avatar-url: /api/files/default-avatar.png
```

### Environment Variables

- `HMS_UPLOAD_DIR`: Base upload directory path (default: `./uploads`)

### Directory Structure

```
uploads/
└── patient-photos/
    ├── patient_<uuid>.jpg          # Original photos
    ├── patient_<uuid>.png
    └── thumbnails/
        ├── patient_<uuid>.jpg      # Thumbnails
        └── patient_<uuid>.png
```

## API Endpoints

### 1. Upload Patient Photo

**Endpoint:** `POST /api/patients/{id}/photo`

**Description:** Upload a new photo for a patient. Automatically generates thumbnail and replaces old photo if exists.

**Request:**
- **Method:** POST
- **Content-Type:** multipart/form-data
- **Path Parameter:** `id` - Patient UUID
- **Form Data:**
  - `file` - Image file to upload

**Example (cURL):**
```bash
curl -X POST \
  http://localhost:8080/api/patients/550e8400-e29b-41d4-a716-446655440000/photo \
  -H "Content-Type: multipart/form-data" \
  -F "file=@/path/to/photo.jpg"
```

**Example (JavaScript/Fetch):**
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);

fetch(`/api/patients/${patientId}/photo`, {
  method: 'POST',
  body: formData
})
.then(response => response.json())
.then(data => console.log(data));
```

**Response:**
```json
{
  "success": true,
  "message": "Foto pasien berhasil diunggah",
  "data": {
    "photoUrl": "/api/patients/photos/patient_550e8400-e29b-41d4-a716-446655440000.jpg",
    "thumbnailUrl": "/api/patients/photos/thumbnails/patient_550e8400-e29b-41d4-a716-446655440000.jpg",
    "originalFilename": "photo.jpg",
    "storedFilename": "patient_550e8400-e29b-41d4-a716-446655440000.jpg",
    "fileSizeBytes": 1048576,
    "fileSize": "1.00 MB",
    "contentType": "image/jpeg",
    "uploadedAt": "2025-01-18T14:30:00"
  }
}
```

**Validation Rules:**
- Maximum file size: 5MB
- Allowed extensions: jpg, jpeg, png, gif, webp
- File must not be empty
- Filename must not contain path traversal sequences

**Error Responses:**
```json
{
  "success": false,
  "message": "File size exceeds maximum allowed size of 5242880 bytes",
  "data": null
}
```

```json
{
  "success": false,
  "message": "File type 'bmp' is not allowed. Allowed types: jpg,jpeg,png,gif,webp",
  "data": null
}
```

### 2. Get Patient Photo

**Endpoint:** `GET /api/patients/photos/{filename}`

**Description:** Retrieve the original patient photo.

**Request:**
- **Method:** GET
- **Path Parameter:** `filename` - Photo filename

**Example:**
```bash
curl -X GET \
  http://localhost:8080/api/patients/photos/patient_550e8400-e29b-41d4-a716-446655440000.jpg
```

**Response:**
- **Content-Type:** image/jpeg, image/png, etc. (based on file)
- **Cache-Control:** max-age=2592000, public (30 days)
- **Content-Disposition:** inline; filename="patient_xxx.jpg"
- **Body:** Image binary data

**HTML Usage:**
```html
<img src="/api/patients/photos/patient_550e8400-e29b-41d4-a716-446655440000.jpg"
     alt="Patient Photo"
     class="patient-photo">
```

### 3. Get Patient Photo Thumbnail

**Endpoint:** `GET /api/patients/photos/thumbnails/{filename}`

**Description:** Retrieve the patient photo thumbnail (150x150 pixels).

**Request:**
- **Method:** GET
- **Path Parameter:** `filename` - Thumbnail filename

**Example:**
```bash
curl -X GET \
  http://localhost:8080/api/patients/photos/thumbnails/patient_550e8400-e29b-41d4-a716-446655440000.jpg
```

**Response:**
- **Content-Type:** image/jpeg, image/png, etc.
- **Cache-Control:** max-age=2592000, public (30 days)
- **Content-Disposition:** inline; filename="thumb_patient_xxx.jpg"
- **Body:** Thumbnail image binary data

**HTML Usage:**
```html
<img src="/api/patients/photos/thumbnails/patient_550e8400-e29b-41d4-a716-446655440000.jpg"
     alt="Patient Thumbnail"
     class="patient-thumbnail"
     width="150"
     height="150">
```

### 4. Delete Patient Photo

**Endpoint:** `DELETE /api/patients/{id}/photo`

**Description:** Delete patient photo and thumbnail (GDPR-compliant). Removes files from filesystem and updates database.

**Request:**
- **Method:** DELETE
- **Path Parameter:** `id` - Patient UUID

**Example:**
```bash
curl -X DELETE \
  http://localhost:8080/api/patients/550e8400-e29b-41d4-a716-446655440000/photo
```

**Response:**
```json
{
  "success": true,
  "message": "Foto pasien berhasil dihapus",
  "data": null
}
```

**JavaScript Example:**
```javascript
fetch(`/api/patients/${patientId}/photo`, {
  method: 'DELETE'
})
.then(response => response.json())
.then(data => {
  console.log('Photo deleted successfully');
});
```

### 5. Get Patient Photo URL

**Endpoint:** `GET /api/patients/{id}/photo-url`

**Description:** Get the photo URL for a patient. Returns default avatar URL if no photo exists.

**Request:**
- **Method:** GET
- **Path Parameter:** `id` - Patient UUID

**Example:**
```bash
curl -X GET \
  http://localhost:8080/api/patients/550e8400-e29b-41d4-a716-446655440000/photo-url
```

**Response (With Photo):**
```json
{
  "success": true,
  "message": "URL foto berhasil diambil",
  "data": "/api/patients/photos/patient_550e8400-e29b-41d4-a716-446655440000.jpg"
}
```

**Response (Without Photo - Default Avatar):**
```json
{
  "success": true,
  "message": "URL foto berhasil diambil",
  "data": "/api/files/default-avatar.png"
}
```

## Frontend Integration Examples

### React Component Example

```jsx
import React, { useState } from 'react';

function PatientPhotoUpload({ patientId }) {
  const [uploading, setUploading] = useState(false);
  const [photoUrl, setPhotoUrl] = useState(null);

  const handleFileUpload = async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    // Validate file size
    if (file.size > 5 * 1024 * 1024) {
      alert('File size must be less than 5MB');
      return;
    }

    // Validate file type
    const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    if (!allowedTypes.includes(file.type)) {
      alert('Only JPG, PNG, GIF, and WEBP files are allowed');
      return;
    }

    setUploading(true);

    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await fetch(`/api/patients/${patientId}/photo`, {
        method: 'POST',
        body: formData
      });

      const result = await response.json();

      if (result.success) {
        setPhotoUrl(result.data.photoUrl);
        alert('Photo uploaded successfully!');
      } else {
        alert(`Upload failed: ${result.message}`);
      }
    } catch (error) {
      console.error('Upload error:', error);
      alert('An error occurred during upload');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm('Are you sure you want to delete this photo?')) return;

    try {
      const response = await fetch(`/api/patients/${patientId}/photo`, {
        method: 'DELETE'
      });

      const result = await response.json();

      if (result.success) {
        setPhotoUrl(null);
        alert('Photo deleted successfully!');
      }
    } catch (error) {
      console.error('Delete error:', error);
    }
  };

  return (
    <div className="patient-photo-upload">
      {photoUrl && (
        <div className="photo-preview">
          <img
            src={photoUrl}
            alt="Patient Photo"
            className="patient-photo"
          />
          <button onClick={handleDelete}>Delete Photo</button>
        </div>
      )}

      <input
        type="file"
        accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
        onChange={handleFileUpload}
        disabled={uploading}
      />

      {uploading && <div>Uploading...</div>}
    </div>
  );
}
```

### Vue.js Component Example

```vue
<template>
  <div class="patient-photo-upload">
    <div v-if="photoUrl" class="photo-preview">
      <img :src="photoUrl" alt="Patient Photo" class="patient-photo" />
      <button @click="deletePhoto">Delete Photo</button>
    </div>

    <input
      type="file"
      accept="image/jpeg,image/jpg,image/png,image/gif,image/webp"
      @change="uploadPhoto"
      :disabled="uploading"
    />

    <div v-if="uploading">Uploading...</div>
  </div>
</template>

<script>
export default {
  props: ['patientId'],
  data() {
    return {
      uploading: false,
      photoUrl: null
    };
  },
  methods: {
    async uploadPhoto(event) {
      const file = event.target.files[0];
      if (!file) return;

      // Validation
      if (file.size > 5 * 1024 * 1024) {
        alert('File size must be less than 5MB');
        return;
      }

      this.uploading = true;

      const formData = new FormData();
      formData.append('file', file);

      try {
        const response = await fetch(`/api/patients/${this.patientId}/photo`, {
          method: 'POST',
          body: formData
        });

        const result = await response.json();

        if (result.success) {
          this.photoUrl = result.data.photoUrl;
          this.$emit('upload-success', result.data);
        } else {
          alert(`Upload failed: ${result.message}`);
        }
      } catch (error) {
        console.error('Upload error:', error);
      } finally {
        this.uploading = false;
      }
    },

    async deletePhoto() {
      if (!confirm('Are you sure?')) return;

      try {
        const response = await fetch(`/api/patients/${this.patientId}/photo`, {
          method: 'DELETE'
        });

        const result = await response.json();

        if (result.success) {
          this.photoUrl = null;
          this.$emit('delete-success');
        }
      } catch (error) {
        console.error('Delete error:', error);
      }
    }
  }
};
</script>
```

## Security Considerations

### 1. **File Validation**
- File size limit enforced (5MB default)
- File extension whitelist (jpg, jpeg, png, gif, webp)
- MIME type validation
- File content validation

### 2. **Path Traversal Prevention**
- Filename sanitization
- Path traversal sequence detection
- Secure filename generation using UUID

### 3. **Access Control**
- Authentication required (to be implemented)
- Authorization checks for photo access
- Patient data privacy enforcement

### 4. **GDPR Compliance**
- Complete data deletion capability
- Right to erasure implementation
- Audit logging
- Data retention policies

## Performance Optimization

### 1. **Caching Strategy**
- HTTP Cache-Control headers (30 days)
- Public caching for CDN compatibility
- Browser caching optimization

### 2. **Thumbnail Usage**
- 150x150 thumbnails for list views
- Reduced bandwidth consumption
- Faster page loading

### 3. **Image Optimization**
- High-quality compression (0.9 quality factor)
- Format-specific optimization
- Aspect ratio preservation

## Error Handling

### Common Errors

**File Too Large:**
```json
{
  "success": false,
  "message": "File size exceeds maximum allowed size of 5242880 bytes",
  "data": null
}
```

**Invalid File Type:**
```json
{
  "success": false,
  "message": "File type 'exe' is not allowed. Allowed types: jpg,jpeg,png,gif,webp",
  "data": null
}
```

**Patient Not Found:**
```json
{
  "success": false,
  "message": "Patient not found with ID: 550e8400-e29b-41d4-a716-446655440000",
  "data": null
}
```

**File Not Found:**
```json
{
  "success": false,
  "message": "File not found with filename: patient_xxx.jpg",
  "data": null
}
```

## Technical Implementation

### Components

1. **FileStorageProperties** - Configuration binding
2. **FileStorageService** - File upload/download/deletion
3. **ImageService** - Thumbnail generation and image processing
4. **PatientService** - Business logic integration
5. **PatientPhotoController** - REST endpoints

### Technologies

- **Thumbnailator 0.4.20** - Image processing
- **Apache Commons IO 2.15.1** - File utilities
- **Spring MultipartFile** - File upload handling
- **Java NIO** - File system operations

### Database Schema

Patient table includes:
```sql
photo_url VARCHAR(500)  -- Stores: /api/patients/photos/patient_<uuid>.jpg
```

## Testing

### Manual Testing with cURL

**Upload:**
```bash
curl -X POST \
  -F "file=@test-photo.jpg" \
  http://localhost:8080/api/patients/{patient-id}/photo
```

**Download:**
```bash
curl -X GET \
  -o downloaded-photo.jpg \
  http://localhost:8080/api/patients/photos/patient_xxx.jpg
```

**Delete:**
```bash
curl -X DELETE \
  http://localhost:8080/api/patients/{patient-id}/photo
```

## Future Enhancements

1. **Cloud Storage Integration**
   - AWS S3 support
   - Google Cloud Storage support
   - Azure Blob Storage support

2. **Advanced Image Processing**
   - Face detection
   - Auto-rotation based on EXIF
   - Smart cropping
   - Watermarking

3. **Multiple Photo Support**
   - Profile photo + ID photo
   - Photo gallery
   - Document attachments

4. **CDN Integration**
   - CloudFlare integration
   - AWS CloudFront support
   - Custom CDN configuration

5. **Image Optimization**
   - WebP format conversion
   - Progressive JPEG
   - Lazy loading support

---

**Version**: 1.0.0
**Last Updated**: 2025-01-18
**Author**: HMS Development Team
