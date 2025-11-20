Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Procedure.html

    {
        "resource": [
            {
                "resourceType": "Procedure",
                "id": "0901R001-1193709-34-55c8999d-788b-43d8-bbd6-070ef25971ad",
                "text": {
                    "status": "generated",
                    "div": "Generated Narrative with Details"
                },
                "status": "completed",
                "code": {
                    "coding": [
                        {
                            "system": "http:\/\/snomed.info\/sct",
                            "code": "PROCx000032816",
                            "display": "Triage"
                        }
                    ]
                },
                "subject": {
                    "reference": "Patient\/0901R001-1193709-34-af125c4b-cd0f-4877-b76a-532a3656da97",
                    "display": "NURUL"
                },
                "context": {
                    "reference": "Encounter\/0901R001-1193709-34-8d887b38-feb9-4edf-b818-c49336448c90",
                    "display": "NURUL encounter on 31 Desember 2018 17:08"
                },
                "performedPeriod": {
                    "start": "2018-12-31 17:08:00",
                    "end": "2018-12-31 17:08:00"
                },
                "performer": [
                    {
                        "role": {
                            "coding": [
                                {
                                    "system": "http:\/\/snomed.info\/sct",
                                    "code": "310512001",
                                    "display": "Medical oncologist"
                                }
                            ]
                        },
                        "actor": {
                            "reference": "Practitioner\/0901R001-1193709-34-cbda45a0-305b-42c1-8a7c-ef6268ec8c0f",
                            "display": "Septi Sari Yanti"
                        }
                    }
                ],
                "reasonCode": [
                    {
                        "text": "DiagnosticReport\/f201"
                    }
                ],
                "bodySite": [
                    {
                        "coding": [
                            {
                                "system": "http:\/\/snomed.info\/sct",
                                "code": "272676008",
                                "display": "Sphenoid bone"
                            }
                        ]
                    }
                ],
                "focalDevice": [
                    {
                        "action": {
                            "coding": [
                                {
                                    "system": "http:\/\/hl7.org\/fhir\/device-action",
                                    "code": "implanted"
                                }
                            ]
                        },
                        "manipulated": {
                            "reference": "Device\/example-pacemaker"
                        }
                    }
                ],
                "note": [
                    {
                        "text": ""
                    }
                ]
            },
            {
                "resourceType": "Procedure",
                "id": "0901R001-1193709-34-519258f6-22d2-4ed0-962f-b1e666d2c9ac",
                "text": {
                    "status": "generated",
                    "div": "Generated Narrative with Details"
                },
                "status": "completed",
                "code": {
                    "coding": [
                        {
                            "system": "http:\/\/snomed.info\/sct",
                            "code": "PROCx000013190",
                            "display": "Administrasi IGD"
                        }
                    ]
                },
                "subject": {
                    "reference": "Patient\/0901R001-1193709-34-af125c4b-cd0f-4877-b76a-532a3656da97",
                    "display": "NURUL"
                },
                "context": {
                    "reference": "Encounter\/0901R001-1193709-34-8d887b38-feb9-4edf-b818-c49336448c90",
                    "display": "NURUL encounter on 31 Desember 2018 17:08"
                },
                "performedPeriod": {
                    "start": "2018-12-31 17:08:00",
                    "end": "2018-12-31 17:08:00"
                },
                "performer": [
                    {
                        "role": {
                            "coding": [
                                {
                                    "system": "http:\/\/snomed.info\/sct",
                                    "code": "310512001",
                                    "display": "Medical oncologist"
                                }
                            ]
                        },
                        "actor": {
                            "reference": "Practitioner\/0901R001-1193709-34-cbda45a0-305b-42c1-8a7c-ef6268ec8c0f",
                            "display": "Septi Sari Yanti"
                        }
                    }
                ],
                "reasonCode": [
                    {
                        "text": "DiagnosticReport\/f201"
                    }
                ],
                "bodySite": [
                    {
                        "coding": [
                            {
                                "system": "http:\/\/snomed.info\/sct",
                                "code": "272676008",
                                "display": "Sphenoid bone"
                            }
                        ]
                    }
                ],
                "focalDevice": [
                    {
                        "action": {
                            "coding": [
                                {
                                    "system": "http:\/\/hl7.org\/fhir\/device-action",
                                    "code": "implanted"
                                }
                            ]
                        },
                        "manipulated": {
                            "reference": "Device\/example-pacemaker"
                        }
                    }
                ],
                "note": [
                    {
                        "text": ""
                    }
                ]
            }
        ]
    }