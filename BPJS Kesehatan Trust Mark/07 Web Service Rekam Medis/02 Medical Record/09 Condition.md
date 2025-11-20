Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Condition.html

    {
        "resource": {
            "resourceType": "Condition",
            "id": "0901R001-1196708-1-f5fd08a5-a274-4c9e-a2bd-b7dc57fda0e2",
            "clinicalStatus": "active",
            "verificationStatus": "confirmed",
            "category": [
                {
                    "coding": [
                        {
                            "system": "http://hl7.org/fhir/condition-category",
                            "code": "encounter-diagnosis",
                            "display": "Encounter Diagnosis"
                        }
                    ]
                }
            ],
            "code": {
                "coding": [
                    {
                        "system": "http://hl7.org/fhir/sid/icd-10",
                        "code": "T31.4",
                        "display": "Burns involving 40-49% of body surface"
                    }
                ],
                "text": "Burns involving 40-49% of body surface"
            },
            "subject": {
                "reference": "Patient/0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c"
            },
            "onsetDateTime": "2018-08-16 07:47:07"
        }
    }