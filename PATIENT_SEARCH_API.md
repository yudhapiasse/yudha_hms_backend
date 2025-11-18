# Patient Search API Documentation

## Overview

This document describes the comprehensive patient search functionality implemented in the HMS backend. The search system provides powerful querying capabilities with multiple search criteria, pagination, sorting, and configurable response depth.

## Features

### 1. **Multi-Field Search**
- Quick search across MRN, NIK, BPJS number, patient name, and phone
- Specific field searches for targeted queries
- Full-text search using PostgreSQL for name matching

### 2. **Advanced Filtering**
- Age range filtering
- Date range filtering (birth date, registration date)
- Geographic filtering (province, city, district)
- Status filtering (active, deceased, VIP, BPJS status)
- Demographic filtering (gender, religion, blood type, marital status)

### 3. **Pagination & Sorting**
- Configurable page size (1-100 records)
- Multiple sort fields supported
- Ascending/descending sort directions

### 4. **Configurable Data Depth**
- **BASIC**: Minimal patient information (id, MRN, name, basic demographics)
- **DETAILED**: Standard information with addresses
- **COMPLETE**: Full patient data including emergency contacts and allergies

### 5. **Barcode/QR Code Generation**
- Generate QR codes for patient MRN
- Generate barcodes for patient cards
- Base64 encoded images ready for embedding in HTML/PDF
- Support for patient ID card generation

## API Endpoints

### 1. Advanced Patient Search

**Endpoint:** `POST /api/patients/search`

**Description:** Search patients with comprehensive filtering and customization options.

**Request Body:**
```json
{
  "searchTerm": "string",              // Quick search across multiple fields
  "mrn": "string",                     // Specific MRN search
  "nik": "string",                     // Specific NIK search
  "bpjsNumber": "string",              // Specific BPJS number search
  "name": "string",                    // Name search (partial match)
  "phone": "string",                   // Phone search (partial match)
  "email": "string",                   // Email search (partial match)
  "gender": "MALE|FEMALE",             // Gender filter
  "birthDateFrom": "2000-01-01",       // Birth date range start
  "birthDateTo": "2024-12-31",         // Birth date range end
  "ageFrom": 18,                       // Minimum age
  "ageTo": 65,                         // Maximum age
  "registrationDateFrom": "2024-01-01",// Registration date range start
  "registrationDateTo": "2024-12-31",  // Registration date range end
  "religionId": "uuid",                // Religion filter
  "bloodTypeId": "uuid",               // Blood type filter
  "maritalStatusId": "uuid",           // Marital status filter
  "provinceId": "uuid",                // Province filter
  "cityId": "uuid",                    // City filter
  "districtId": "uuid",                // District filter
  "isActive": true,                    // Active status filter
  "isDeceased": false,                 // Deceased status filter
  "isVip": false,                      // VIP status filter
  "bpjsActive": true,                  // BPJS active status filter
  "bpjsClass": "Kelas 1",             // BPJS class filter
  "page": 0,                           // Page number (0-indexed)
  "size": 20,                          // Page size (1-100)
  "sortBy": "createdAt",               // Sort field
  "sortDirection": "DESC",             // Sort direction (ASC|DESC)
  "dataDepth": "DETAILED"              // Data depth (BASIC|DETAILED|COMPLETE)
}
```

**Response:**
```json
{
  "success": true,
  "message": "Ditemukan 150 pasien (halaman 1 dari 8)",
  "data": {
    "patients": [
      {
        "id": "uuid",
        "mrn": "202501-00001",
        "nik": "3201234567890123",
        "bpjsNumber": "0001234567890",
        "fullName": "John Doe",
        "birthDate": "1990-05-15",
        "age": 34,
        "gender": "MALE",
        "phonePrimary": "081234567890",
        "addresses": [...],
        // ... more fields based on data depth
      }
    ],
    "currentPage": 0,
    "pageSize": 20,
    "totalRecords": 150,
    "totalPages": 8,
    "isFirst": true,
    "isLast": false,
    "hasNext": true,
    "hasPrevious": false,
    "numberOfRecords": 20,
    "metadata": {
      "executionTimeMs": 45,
      "dataDepth": "DETAILED",
      "sortBy": "createdAt",
      "sortDirection": "DESC",
      "quickSearchUsed": false,
      "advancedFiltersUsed": true
    }
  }
}
```

### 2. Quick Search

**Endpoint:** `GET /api/patients/search/quick?term={term}&page={page}&size={size}`

**Description:** Simple search across MRN, NIK, BPJS number, name, and phone.

**Query Parameters:**
- `term` (required): Search term
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size

**Example:**
```bash
GET /api/patients/search/quick?term=John&page=0&size=10
```

**Response:** Same structure as advanced search with DETAILED data depth.

### 3. Search by Name

**Endpoint:** `GET /api/patients/search/name?name={name}&page={page}&size={size}`

**Description:** Search patients by name using full-text search.

**Query Parameters:**
- `name` (required): Patient name
- `page` (optional, default: 0): Page number
- `size` (optional, default: 20): Page size

**Example:**
```bash
GET /api/patients/search/name?name=Ahmad&page=0&size=10
```

**Response:** Same structure as advanced search with DETAILED data depth.

### 4. Generate Patient QR Code

**Endpoint:** `GET /api/patients/{id}/qrcode`

**Description:** Generate QR code for patient MRN.

**Path Parameters:**
- `id` (required): Patient UUID

**Response:**
```json
{
  "success": true,
  "message": "QR code berhasil dibuat",
  "data": {
    "mrn": "202501-00001",
    "qrCode": "data:image/png;base64,iVBORw0KG...",
    "barcode": "data:image/png;base64,iVBORw0KG..."
  }
}
```

### 5. Generate QR Code by MRN

**Endpoint:** `GET /api/patients/mrn/{mrn}/qrcode`

**Description:** Generate QR code using MRN directly.

**Path Parameters:**
- `mrn` (required): Medical Record Number

**Example:**
```bash
GET /api/patients/mrn/202501-00001/qrcode
```

**Response:** Same as Generate Patient QR Code

### 6. Generate Patient Card Data

**Endpoint:** `GET /api/patients/{id}/card`

**Description:** Generate complete patient card data with QR code and barcode.

**Path Parameters:**
- `id` (required): Patient UUID

**Response:**
```json
{
  "success": true,
  "message": "Data kartu pasien berhasil dibuat",
  "data": {
    "mrn": "202501-00001",
    "qrCode": "data:image/png;base64,iVBORw0KG...",
    "barcode": "data:image/png;base64,iVBORw0KG..."
  }
}
```

## Usage Examples

### Example 1: Search Active BPJS Patients

```bash
curl -X POST http://localhost:8080/api/patients/search \
  -H "Content-Type: application/json" \
  -d '{
    "bpjsActive": true,
    "isActive": true,
    "page": 0,
    "size": 20,
    "sortBy": "fullName",
    "sortDirection": "ASC",
    "dataDepth": "BASIC"
  }'
```

### Example 2: Search Patients by Age Range

```bash
curl -X POST http://localhost:8080/api/patients/search \
  -H "Content-Type: application/json" \
  -d '{
    "ageFrom": 18,
    "ageTo": 65,
    "gender": "MALE",
    "page": 0,
    "size": 50,
    "dataDepth": "DETAILED"
  }'
```

### Example 3: Search Patients Registered in 2024

```bash
curl -X POST http://localhost:8080/api/patients/search \
  -H "Content-Type: application/json" \
  -d '{
    "registrationDateFrom": "2024-01-01",
    "registrationDateTo": "2024-12-31",
    "page": 0,
    "size": 100,
    "sortBy": "registrationDate",
    "sortDirection": "DESC"
  }'
```

### Example 4: Quick Search

```bash
curl -X GET "http://localhost:8080/api/patients/search/quick?term=Ahmad&page=0&size=10"
```

### Example 5: Search by Name

```bash
curl -X GET "http://localhost:8080/api/patients/search/name?name=Budi&page=0&size=10"
```

### Example 6: Generate Patient QR Code

```bash
curl -X GET "http://localhost:8080/api/patients/{patient-id}/qrcode"
```

### Example 7: Search by Province and City

```bash
curl -X POST http://localhost:8080/api/patients/search \
  -H "Content-Type: application/json" \
  -d '{
    "provinceId": "province-uuid",
    "cityId": "city-uuid",
    "page": 0,
    "size": 20,
    "dataDepth": "DETAILED"
  }'
```

## Data Depth Comparison

### BASIC
Returns minimal patient information suitable for lists:
- id, mrn, nik, bpjsNumber
- fullName, birthDate, age, gender
- phonePrimary, photoUrl
- Status flags (isActive, isDeceased, isVip, bpjsActive, bpjsClass)

### DETAILED (Default)
Includes BASIC fields plus:
- Complete demographic information
- Religion, marital status, blood type
- Education, occupation
- All contact information
- Patient addresses (KTP and Domicile)
- Registration information

### COMPLETE
Includes DETAILED fields plus:
- Emergency contacts
- Patient allergies
- Complete audit information
- All related data

## Performance Considerations

1. **Indexing**: Database indexes are applied on:
   - MRN, NIK, BPJS number (unique indexes)
   - Patient name (trigram index for full-text search)
   - Birth date, registration date, gender
   - Geographic fields (province, city, district)

2. **Pagination**: Always use pagination to limit result sets. Default page size is 20, maximum is 100.

3. **Data Depth**: Use BASIC data depth for list views, DETAILED for most cases, and COMPLETE only when all data is needed.

4. **Query Optimization**: The search uses JPA Specifications with dynamic queries. Complex filters may take longer. Monitor execution times using the `metadata.executionTimeMs` field.

## Implementation Details

### Technologies Used
- **Spring Data JPA Specifications**: For dynamic query building
- **PostgreSQL**: Database with full-text search capabilities
- **ZXing**: Barcode and QR code generation library
- **Lombok**: For reducing boilerplate code

### Key Components

1. **PatientSearchCriteria**: DTO for search parameters
2. **PatientSearchResponse**: Generic response wrapper with pagination metadata
3. **PatientSpecification**: JPA Specification builder for dynamic queries
4. **PatientRepository**: Extended with JpaSpecificationExecutor
5. **BarcodeService**: QR code and barcode generation
6. **PatientService**: Business logic for search operations
7. **PatientController**: REST endpoints

### Database Query Example

The system translates search criteria into optimized SQL:

```sql
SELECT DISTINCT p.*
FROM patient_schema.patient p
LEFT JOIN patient_schema.patient_address pa ON p.id = pa.patient_id
WHERE p.deleted_at IS NULL
  AND (
    LOWER(p.mrn) LIKE '%search%'
    OR LOWER(p.nik) LIKE '%search%'
    OR LOWER(p.bpjs_number) LIKE '%search%'
    OR LOWER(p.full_name) LIKE '%search%'
  )
  AND p.is_active = true
  AND p.birth_date >= '1990-01-01'
  AND p.birth_date <= '2000-12-31'
ORDER BY p.created_at DESC
LIMIT 20 OFFSET 0;
```

## Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

Common errors:
- **400 Bad Request**: Invalid search criteria
- **404 Not Found**: Patient not found (for specific searches)
- **500 Internal Server Error**: Server error during search

## Future Enhancements

1. **Elasticsearch Integration**: For advanced full-text search
2. **Search History**: Track and save frequent searches
3. **Export Functionality**: Export search results to Excel/PDF
4. **Saved Filters**: Allow users to save commonly used filter combinations
5. **Fuzzy Matching**: Implement phonetic search for name matching
6. **Advanced Analytics**: Search result analytics and reporting

## Support

For issues or questions:
- Check logs for detailed error messages
- Verify database connectivity
- Ensure proper permissions for search operations
- Monitor query execution times in production

---

**Version**: 1.0.0
**Last Updated**: 2025-01-18
**Author**: HMS Development Team
