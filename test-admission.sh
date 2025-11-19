#!/bin/bash

BASE_URL="http://localhost:8080"
AUTH="user:aa16f339-8441-4b99-84c3-843da86daa10"
PATIENT_ID="b1678c6b-dc0d-45c5-a5bf-106c6d14179a"

echo "=========================================="
echo "Testing Inpatient Admission System"
echo "=========================================="
echo

echo "1. Testing: Get Available VIP Rooms"
echo "------------------------------------------"
curl -s -u "$AUTH" "$BASE_URL/api/rooms/available/VIP" | jq .
echo
echo

echo "2. Testing: Get Room Occupancy Statistics"
echo "------------------------------------------"
curl -s -u "$AUTH" "$BASE_URL/api/rooms/occupancy" | jq .
echo
echo

echo "3. Testing: Create Inpatient Admission"
echo "------------------------------------------"
ADMISSION_DATA=$(cat <<EOF
{
  "patientId": "$PATIENT_ID",
  "admissionType": "EMERGENCY",
  "roomClass": "KELAS_2",
  "chiefComplaint": "Chest pain and shortness of breath",
  "diagnoses": [
    {
      "diagnosisType": "PRIMARY",
      "icd10Code": "I21.9",
      "icd10Description": "Acute myocardial infarction, unspecified"
    }
  ],
  "referringDoctor": "Dr. John Smith",
  "referringFacility": "Klinik Sehat Sejahtera",
  "estimatedLengthOfStayDays": 5,
  "depositPaid": 1200000,
  "depositDays": 3,
  "paymentMethod": "CASH",
  "specialNeeds": "Requires cardiac monitoring"
}
EOF
)

ADMISSION_RESPONSE=$(curl -s -u "$AUTH" -X POST "$BASE_URL/api/admissions" \
  -H "Content-Type: application/json" \
  -d "$ADMISSION_DATA")

echo "$ADMISSION_RESPONSE" | jq .

# Extract admission ID for next tests
ADMISSION_ID=$(echo "$ADMISSION_RESPONSE" | jq -r '.data.id')
echo
echo "Created Admission ID: $ADMISSION_ID"
echo
echo

echo "4. Testing: Get Admission Details"
echo "------------------------------------------"
curl -s -u "$AUTH" "$BASE_URL/api/admissions/$ADMISSION_ID" | jq .
echo
echo

echo "5. Testing: Get All Active Admissions"
echo "------------------------------------------"
curl -s -u "$AUTH" "$BASE_URL/api/admissions/active" | jq .
echo
echo

echo "6. Testing: Generate Wristband Data"
echo "------------------------------------------"
curl -s -u "$AUTH" "$BASE_URL/api/admissions/$ADMISSION_ID/wristband" | jq .
echo
echo

echo "=========================================="
echo "Testing Complete!"
echo "=========================================="
