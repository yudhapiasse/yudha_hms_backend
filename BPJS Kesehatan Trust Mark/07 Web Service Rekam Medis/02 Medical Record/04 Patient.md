Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/patient.html


    {
        "resource": {
            "resourceType": "Patient",
            "id": "0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c",
            "identifier": [
                {
                    "use": "usual",
                    "type": {
                        "coding": [
                            {
                                "system": "http://hl7.org/fhir/v2/0203",
                                "code": "MR"
                            }
                        ]
                    },
                    "value": "429-83-94",
                    "assigner": {
                        "display": "RSUPN DR CIPTO"
                    }
                },
                {
                    "use": "official",
                    "type": {
                        "coding": [
                            {
                                "system": "http://hl7.org/fhir/v2/0203",
                                "code": "MB"
                            }
                        ]
                    },
                    "value": "9901887833827",
                    "assigner": {
                        "display": "BPJS KESEHATAN"
                    }
                },
                {
                    "use": "official",
                    "type": {
                        "coding": [
                            {
                                "system": "http://hl7.org/fhir/v2/0203",
                                "code": "NNIDN"
                            }
                        ]
                    },
                    "value": "3175098905951002",
                    "assigner": {
                        "display": "KEMENDAGRI"
                    }
                }
            ],
            "active": true,
            "name": [
                {
                    "use": "official",
                    "text": "BASONI"
                }
            ],
            "maritalStatus": {
                "coding": [
                    {
                        "system": "http://hl7.org/fhir/v3/MaritalStatus",
                        "code": "U"
                    }
                ]
            },
            "telecom": [
                {
                    "system": "phone",
                    "value": "",
                    "use": "work"
                },
                {
                    "system": "phone",
                    "value": "021773842888",
                    "use": "mobile"
                },
                {
                    "system": "phone",
                    "value": "TDK ADA",
                    "use": "home"
                }
            ],
            "gender": "male",
            "birthDate": "1995-05-15",
            "deceasedBoolean": false,
            "address": [
                {
                    "line": [
                        "JL JAYA NO. 119 RT 10000 RW 003"
                    ],
                    "city": "",
                    "district": "PULO GADING",
                    "state": "",
                    "postalCode": "1311260",
                    "text": "JL JAYA NO. 1000 RT 012 RW 003",
                    "use": "home",
                    "type": "both"
                }
            ],
            "managingOrganization": {
                "reference": "Organization/0901R001-1196708-1-ebbc9d66-ba73-404c-9f8f-655e2726638e",
                "display": "RSCM"
            }
        }
    }