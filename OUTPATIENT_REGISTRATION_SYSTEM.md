# Outpatient Registration System (Rawat Jalan)

## Overview

Comprehensive outpatient registration system for Indonesian hospitals with support for:
- Multiple polyclinics (Poli Umum, Poli Anak, Poli Kandungan, etc.)
- Doctor scheduling and availability
- Queue management
- Appointment booking
- Walk-in registrations
- BPJS integration
- Registration fee calculation

## Database Schema

### Tables Created (V5 Migration)

#### 1. **registration_schema.polyclinic**
Stores polyclinic master data.

**Key Fields:**
- `code` - Unique polyclinic code (e.g., "POLI-UMUM")
- `name` - Polyclinic name (e.g., "Poli Umum")
- `operating_days` - JSON array of operating days
- `opening_time` / `closing_time` - Operating hours
- `max_patients_per_day` - Daily capacity
- `base_registration_fee` - Base registration fee
- `allow_walk_in` / `allow_appointments` - Registration type flags

**Sample Data:**
```sql
- POLI-UMUM (Poli Umum) - Rp 25,000
- POLI-ANAK (Poli Anak) - Rp 30,000
- POLI-KAND (Poli Kandungan) - Rp 35,000
- POLI-GIGI (Poli Gigi) - Rp 30,000
- POLI-MATA (Poli Mata) - Rp 35,000
- POLI-THT (Poli THT) - Rp 35,000
- POLI-JANT (Poli Jantung) - Rp 50,000
- POLI-PENY (Poli Penyakit Dalam) - Rp 40,000
```

#### 2. **registration_schema.doctor**
Stores doctor master data with professional details.

**Key Fields:**
- `str_number` - Medical license number (Surat Tanda Registrasi)
- `sip_number` - Practice permit (Surat Izin Praktik)
- `full_name` - Doctor's full name
- `title` - Professional title (dr., dr. Sp.A, etc.)
- `specialization` - Medical specialization
- `base_consultation_fee` - Regular consultation fee
- `bpjs_consultation_fee` - BPJS consultation fee

**Sample Data:**
```sql
- Dr. Ahmad Hidayat (Dokter Umum) - Rp 100,000
- Dr. Siti Nurhaliza, Sp.A (Spesialis Anak) - Rp 200,000
- Dr. Budi Santoso, Sp.OG (Spesialis Kandungan) - Rp 250,000
- Dr. Lisa Permata, Sp.Gigi (Dokter Gigi) - Rp 150,000
```

#### 3. **registration_schema.doctor_schedule**
Stores doctor practice schedules per polyclinic.

**Key Fields:**
- `doctor_id` - Reference to doctor
- `polyclinic_id` - Reference to polyclinic
- `day_of_week` - Day of practice (MONDAY, TUESDAY, etc.)
- `start_time` / `end_time` - Practice hours
- `max_patients` - Maximum patients per session
- `appointment_duration_minutes` - Duration per appointment

**Features:**
- Multiple schedules per doctor (different polyclinics/days)
- Temporary schedule support (effective_date, expiry_date)
- Capacity management per schedule

#### 4. **registration_schema.outpatient_registration**
Main registration records table.

**Key Fields:**
- `registration_number` - Unique registration number
- `patient_id` - Reference to patient
- `polyclinic_id` - Selected polyclinic
- `doctor_id` - Selected doctor
- `registration_type` - WALK_IN or APPOINTMENT
- `appointment_date` / `appointment_time` - For appointments
- `queue_number` - Queue number assigned
- `queue_code` - Queue code (e.g., "A001")
- `status` - REGISTERED, WAITING, IN_CONSULTATION, COMPLETED, CANCELLED
- `payment_method` - CASH, BPJS, INSURANCE, DEBIT, CREDIT
- `is_bpjs` - BPJS patient flag
- `registration_fee` - Registration fee amount
- `consultation_fee` - Consultation fee amount
- `total_fee` - Total fee

**Status Flow:**
```
REGISTERED → WAITING → IN_CONSULTATION → COMPLETED
           ↓
        CANCELLED
```

#### 5. **registration_schema.queue_sequence**
Tracks queue numbers per polyclinic per day.

**Key Fields:**
- `polyclinic_id` - Reference to polyclinic
- `queue_date` - Queue date
- `last_queue_number` - Last assigned queue number
- `prefix` - Queue prefix (e.g., "A", "B", "UM")

**Unique Constraint:** (polyclinic_id, queue_date)

## Features Implementation

### 1. Polyclinic Selection

**Endpoints:**
```
GET /api/outpatient/polyclinics
GET /api/outpatient/polyclinics/active
GET /api/outpatient/polyclinics/{id}
GET /api/outpatient/polyclinics/{id}/available-today
```

**Features:**
- List all polyclinics
- Filter by active status
- Check operating hours
- View daily capacity

**Response Example:**
```json
{
  "id": "uuid",
  "code": "POLI-UMUM",
  "name": "Poli Umum",
  "description": "Poliklinik Umum untuk pemeriksaan kesehatan umum",
  "floor_location": "Lantai 1",
  "building": "Gedung A",
  "operating_days": ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY"],
  "opening_time": "08:00:00",
  "closing_time": "16:00:00",
  "max_patients_per_day": 50,
  "base_registration_fee": 25000,
  "is_active": true,
  "is_open_today": true,
  "available_slots": 35
}
```

### 2. Doctor Selection with Schedule Availability

**Endpoints:**
```
GET /api/outpatient/doctors
GET /api/outpatient/doctors/by-polyclinic/{polyclinicId}
GET /api/outpatient/doctors/{id}/schedules
GET /api/outpatient/doctors/{id}/available-slots?date=2025-01-20
```

**Features:**
- Filter doctors by polyclinic
- View doctor schedules
- Check availability for specific dates
- See available time slots

**Response Example:**
```json
{
  "doctor": {
    "id": "uuid",
    "full_name": "Dr. Ahmad Hidayat",
    "title": "dr.",
    "specialization": "Dokter Umum",
    "base_consultation_fee": 100000
  },
  "available_slots": [
    {
      "time": "08:00",
      "available": true,
      "slots_remaining": 3
    },
    {
      "time": "08:15",
      "available": true,
      "slots_remaining": 3
    },
    {
      "time": "08:30",
      "available": false,
      "slots_remaining": 0
    }
  ]
}
```

### 3. Queue Number Generation

**Algorithm:**
```
Queue Number Format: {PREFIX}{NUMBER}
Examples: A001, A002, ..., A999
          UM001, AN015, KD030

Prefix mapping:
- POLI-UMUM → "UM" or "A"
- POLI-ANAK → "AN" or "B"
- POLI-KAND → "KD" or "C"
```

**Features:**
- Auto-increment per polyclinic per day
- Unique queue numbers
- Reset daily
- Prefix customization

**Generation Logic:**
```java
public String generateQueueNumber(UUID polyclinicId, LocalDate date) {
    // Get or create queue sequence
    QueueSequence sequence = getOrCreateSequence(polyclinicId, date);

    // Increment counter
    int nextNumber = sequence.getLastQueueNumber() + 1;
    sequence.setLastQueueNumber(nextNumber);

    // Format: PREFIX + 3-digit number
    String queueCode = String.format("%s%03d", sequence.getPrefix(), nextNumber);

    return queueCode;
}
```

### 4. Appointment Booking with Time Slots

**Endpoint:**
```
POST /api/outpatient/registrations/appointment
```

**Request:**
```json
{
  "patient_id": "uuid",
  "polyclinic_id": "uuid",
  "doctor_id": "uuid",
  "appointment_date": "2025-01-20",
  "appointment_time": "09:00",
  "payment_method": "CASH",
  "is_bpjs": false,
  "chief_complaint": "Demam dan batuk sejak 3 hari yang lalu"
}
```

**Validation:**
- Check doctor schedule availability
- Verify polyclinic operating hours
- Check slot availability
- Validate appointment date (not in past)
- Check maximum capacity

**Response:**
```json
{
  "success": true,
  "message": "Appointment booked successfully",
  "data": {
    "registration_number": "REG-20250120-001",
    "queue_number": 5,
    "queue_code": "A005",
    "appointment_date": "2025-01-20",
    "appointment_time": "09:00",
    "polyclinic_name": "Poli Umum",
    "doctor_name": "Dr. Ahmad Hidayat",
    "registration_fee": 25000,
    "consultation_fee": 100000,
    "total_fee": 125000,
    "status": "REGISTERED"
  }
}
```

### 5. Walk-in Registration

**Endpoint:**
```
POST /api/outpatient/registrations/walk-in
```

**Request:**
```json
{
  "patient_id": "uuid",
  "polyclinic_id": "uuid",
  "doctor_id": "uuid",
  "payment_method": "BPJS",
  "is_bpjs": true,
  "bpjs_card_number": "0001234567890",
  "chief_complaint": "Kontrol rutin hipertensi"
}
```

**Features:**
- Immediate registration
- Auto-assign queue number
- Check doctor availability TODAY
- Validate polyclinic open status

**Response:**
```json
{
  "success": true,
  "message": "Walk-in registration completed",
  "data": {
    "registration_number": "REG-20250119-045",
    "queue_number": 12,
    "queue_code": "A012",
    "registration_date": "2025-01-19",
    "polyclinic_name": "Poli Umum",
    "doctor_name": "Dr. Ahmad Hidayat",
    "registration_fee": 0,
    "consultation_fee": 0,
    "total_fee": 0,
    "status": "REGISTERED",
    "estimated_wait_time_minutes": 180
  }
}
```

### 6. Registration Fee Calculation

**Fee Structure:**
```
Total Fee = Registration Fee + Consultation Fee

For BPJS patients:
- Registration Fee = 0
- Consultation Fee = 0

For Non-BPJS patients:
- Registration Fee = Polyclinic base fee
- Consultation Fee = Doctor consultation fee
```

**Calculation Logic:**
```java
public RegistrationFeeDto calculateFees(UUID polyclinicId, UUID doctorId, boolean isBpjs) {
    Polyclinic polyclinic = getPolyclinic(polyclinicId);
    Doctor doctor = getDoctor(doctorId);

    if (isBpjs) {
        return RegistrationFeeDto.builder()
            .registrationFee(0)
            .consultationFee(0)
            .totalFee(0)
            .build();
    }

    BigDecimal registrationFee = polyclinic.getBaseRegistrationFee();
    BigDecimal consultationFee = doctor.getBaseConsultationFee();
    BigDecimal totalFee = registrationFee.add(consultationFee);

    return RegistrationFeeDto.builder()
        .registrationFee(registrationFee)
        .consultationFee(consultationFee)
        .totalFee(totalFee)
        .build();
}
```

### 7. Queue Ticket Printing

**Endpoint:**
```
GET /api/outpatient/registrations/{id}/print-ticket
```

**Ticket Format:**
```
╔════════════════════════════════════╗
║    RUMAH SAKIT UMUM MAKASSAR      ║
║                                    ║
║        NOMOR ANTRIAN PASIEN        ║
║                                    ║
║            [  A012  ]              ║
║                                    ║
║ Tanggal: 19 Januari 2025          ║
║ Waktu: 10:30 WITA                 ║
║                                    ║
║ Poliklinik: Poli Umum              ║
║ Dokter: Dr. Ahmad Hidayat          ║
║ Lokasi: Gedung A - Lantai 1       ║
║                                    ║
║ No. Registrasi:                    ║
║ REG-20250119-045                   ║
║                                    ║
║ Pasien: Budi Santoso               ║
║ No. RM: 202501-00015               ║
║                                    ║
║ Estimasi Waktu Tunggu: 3 jam      ║
║                                    ║
║ Harap menunggu panggilan di        ║
║ ruang tunggu poliklinik            ║
╚════════════════════════════════════╝
```

**Response:**
```json
{
  "success": true,
  "message": "Queue ticket generated",
  "data": {
    "ticket_html": "<html>...</html>",
    "ticket_pdf_base64": "JVBERi0xLjQK...",
    "queue_code": "A012",
    "registration_number": "REG-20250119-045",
    "patient_name": "Budi Santoso",
    "polyclinic_name": "Poli Umum",
    "doctor_name": "Dr. Ahmad Hidayat",
    "estimated_wait_time_minutes": 180
  }
}
```

## Validation Rules

### 1. Doctor Schedule Validation

**Rules:**
- Doctor must have active schedule for selected day
- Selected time must be within doctor's practice hours
- Check if schedule is within effective/expiry date range
- Verify maximum patient capacity not exceeded

**Implementation:**
```java
public boolean isDoctorAvailable(UUID doctorId, UUID polyclinicId, LocalDate date, LocalTime time) {
    // Get day of week
    DayOfWeek dayOfWeek = date.getDayOfWeek();

    // Find active schedule
    Optional<DoctorSchedule> schedule = scheduleRepository.findByDoctorAndPolyclinicAndDay(
        doctorId, polyclinicId, dayOfWeek.name()
    );

    if (schedule.isEmpty()) {
        return false;
    }

    DoctorSchedule doctorSchedule = schedule.get();

    // Check time within schedule
    if (time.isBefore(doctorSchedule.getStartTime()) ||
        time.isAfter(doctorSchedule.getEndTime())) {
        return false;
    }

    // Check effective/expiry dates
    if (doctorSchedule.getEffectiveDate() != null &&
        date.isBefore(doctorSchedule.getEffectiveDate())) {
        return false;
    }

    if (doctorSchedule.getExpiryDate() != null &&
        date.isAfter(doctorSchedule.getExpiryDate())) {
        return false;
    }

    // Check capacity
    long registrationCount = registrationRepository.countByDoctorAndDate(doctorId, date);
    if (registrationCount >= doctorSchedule.getMaxPatients()) {
        return false;
    }

    return true;
}
```

### 2. Polyclinic Operating Hours Validation

**Rules:**
- Polyclinic must be active
- Must be operating on selected day
- Current time must be within operating hours
- Check daily capacity not exceeded

**Implementation:**
```java
public boolean isPolyclinicOpen(UUID polyclinicId, LocalDate date) {
    Polyclinic polyclinic = getPolyclinic(polyclinicId);

    // Check active status
    if (!polyclinic.getIsActive()) {
        return false;
    }

    // Check operating day
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    List<String> operatingDays = parseOperatingDays(polyclinic.getOperatingDays());

    if (!operatingDays.contains(dayOfWeek.name())) {
        return false;
    }

    // For today, check current time
    if (date.equals(LocalDate.now())) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(polyclinic.getOpeningTime()) ||
            now.isAfter(polyclinic.getClosingTime())) {
            return false;
        }
    }

    // Check daily capacity
    long registrationCount = registrationRepository.countByPolyclinicAndDate(polyclinicId, date);
    if (registrationCount >= polyclinic.getMaxPatientsPerDay()) {
        return false;
    }

    return true;
}
```

### 3. Appointment Time Slot Validation

**Rules:**
- Appointment must be in the future (not today for appointments)
- Time slot must be available
- Must match appointment duration intervals
- Check double booking prevention

**Implementation:**
```java
public boolean isTimeSlotAvailable(UUID doctorId, LocalDate date, LocalTime time) {
    // Check not in past
    LocalDateTime requestedDateTime = LocalDateTime.of(date, time);
    if (requestedDateTime.isBefore(LocalDateTime.now())) {
        return false;
    }

    // Get appointment duration
    DoctorSchedule schedule = getDoctorSchedule(doctorId, date);
    int duration = schedule.getAppointmentDurationMinutes();

    // Calculate end time
    LocalTime endTime = time.plusMinutes(duration);

    // Check for overlapping appointments
    List<OutpatientRegistration> existingAppointments =
        registrationRepository.findByDoctorAndDateAndTimeRange(
            doctorId, date, time, endTime
        );

    return existingAppointments.isEmpty();
}
```

## API Endpoints Summary

### Polyclinic Endpoints
```
GET    /api/outpatient/polyclinics                    - List all polyclinics
GET    /api/outpatient/polyclinics/active             - List active polyclinics
GET    /api/outpatient/polyclinics/{id}               - Get polyclinic details
GET    /api/outpatient/polyclinics/{id}/schedule      - Get polyclinic schedule
POST   /api/outpatient/polyclinics                    - Create polyclinic (admin)
PUT    /api/outpatient/polyclinics/{id}               - Update polyclinic (admin)
DELETE /api/outpatient/polyclinics/{id}               - Delete polyclinic (admin)
```

### Doctor Endpoints
```
GET    /api/outpatient/doctors                        - List all doctors
GET    /api/outpatient/doctors/active                 - List active doctors
GET    /api/outpatient/doctors/{id}                   - Get doctor details
GET    /api/outpatient/doctors/by-polyclinic/{id}     - Get doctors by polyclinic
GET    /api/outpatient/doctors/{id}/schedules         - Get doctor schedules
GET    /api/outpatient/doctors/{id}/available-slots   - Get available time slots
POST   /api/outpatient/doctors                        - Create doctor (admin)
PUT    /api/outpatient/doctors/{id}                   - Update doctor (admin)
```

### Registration Endpoints
```
POST   /api/outpatient/registrations/walk-in          - Walk-in registration
POST   /api/outpatient/registrations/appointment      - Appointment booking
GET    /api/outpatient/registrations/{id}             - Get registration details
GET    /api/outpatient/registrations                  - List registrations (filtered)
PUT    /api/outpatient/registrations/{id}/check-in    - Patient check-in
PUT    /api/outpatient/registrations/{id}/start       - Start consultation
PUT    /api/outpatient/registrations/{id}/complete    - Complete consultation
PUT    /api/outpatient/registrations/{id}/cancel      - Cancel registration
GET    /api/outpatient/registrations/{id}/print-ticket - Print queue ticket
```

### Queue Endpoints
```
GET    /api/outpatient/queues/today                   - Today's queues
GET    /api/outpatient/queues/by-polyclinic/{id}      - Queues by polyclinic
GET    /api/outpatient/queues/current-number/{id}     - Current serving number
POST   /api/outpatient/queues/call-next               - Call next patient
GET    /api/outpatient/queues/display/{polyclinicId}  - Queue display board
```

### Fee Calculation Endpoints
```
POST   /api/outpatient/fees/calculate                 - Calculate registration fee
GET    /api/outpatient/fees/polyclinic/{id}           - Get polyclinic fees
GET    /api/outpatient/fees/doctor/{id}               - Get doctor consultation fees
```

## Business Rules

### 1. Walk-in vs Appointment
- **Walk-in**: Same-day registration, queue number assigned immediately
- **Appointment**: Future date booking, specific time slot reserved
- Appointments take priority over walk-ins at scheduled time

### 2. BPJS Patients
- Zero registration fee
- Zero consultation fee
- Must provide valid BPJS card number
- Separate quota management

### 3. Queue Management
- Queue resets daily
- Separate queue per polyclinic
- Sequential numbering
- Priority handling for emergencies (future feature)

### 4. Cancellation Policy
- Appointments can be cancelled up to 2 hours before scheduled time
- Walk-in registrations can be cancelled before check-in
- Cancellation reasons required
- Audit trail maintained

## Integration Points

### 1. Patient Management
- Link to patient master data (patient_id)
- Patient demographic information
- Medical history access
- BPJS verification

### 2. Clinical Workflow
- Transition to consultation (clinical_schema)
- Medical record creation
- Prescription management
- Laboratory/radiology referrals

### 3. Billing System
- Fee calculation
- Payment processing
- Receipt generation
- Insurance claims (BPJS)

### 4. Reporting
- Daily registration statistics
- Polyclinic utilization
- Doctor productivity
- Revenue reports
- Queue analytics

## Next Steps

1. **Implement Entity Classes** - Create JPA entities for all tables
2. **Create Repositories** - Spring Data JPA repositories
3. **Build Service Layer** - Business logic implementation
4. **Develop REST Controllers** - API endpoints
5. **Add Validation Logic** - Schedule and operating hours validation
6. **Queue Management** - Queue number generation service
7. **Ticket Generation** - PDF/Print functionality
8. **Testing** - Unit and integration tests
9. **Frontend Integration** - UI for registration desk

## Technology Stack

- **Database**: PostgreSQL 16 with Flyway migrations
- **Backend**: Spring Boot 3.4, Java 21
- **ORM**: Hibernate/JPA
- **Validation**: Jakarta Validation
- **Documentation**: OpenAPI/Swagger
- **PDF Generation**: iText or Flying Saucer (for tickets)

---

**Version**: 1.0.0
**Status**: Database Schema Completed
**Last Updated**: 2025-01-19
**Author**: HMS Development Team
