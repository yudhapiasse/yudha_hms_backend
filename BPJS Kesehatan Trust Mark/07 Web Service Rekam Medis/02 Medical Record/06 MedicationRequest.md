Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/MedicationRequest.html


    {	
        "resource": [
            {
                "resourceType": "MedicationRequest",
                "text": {
                    "div": " TETAGAM P SOLUTION, INJECTION 250 IU/1 ML"
                },
                "identifier": {
                    "system": "id_resep_pulang",
                    "value": "0901R001-1196708-1-e2e4c9b3-fc32-42d5-a693-32a370891560"
                },
                "subject": {
                    "display": "BASONI",
                    "reference": "Patient/0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c"
                },
                "intent": "final",
                "medicationCodeableConcept": {
                    "coding": [
                        {
                            "code": "DRx0006657",
                            "system": "http://rscm.co.id/drug"
                        }
                    ],
                    "text": "TETAGAM P SOLUTION, INJECTION 250 IU/1 ML"
                },
                "dosageInstruction": [
                    {
                        "doseQuantity": {
                            "code": "AMP",
                            "system": "http://unitsofmeasure.org",
                            "unit": "AMP",
                            "value": "1"
                        },
                        "route": {
                            "coding": [
                                {
                                    "code": "002",
                                    "display": "INTRAVENOUS",
                                    "system": "http://snomed.info/sct"
                                }
                            ]
                        },
                        "timing": {
                            "repeat": {
                                "frequency": "1",
                                "period": 1,
                                "periodUnit": "na"
                            }
                        },
                        "additionalInstruction": [
                            {
                                "text": "1 kali"
                            }
                        ]
                    }
                ],
                "reasonCode": [
                    {
                        "coding": [
                            {
                                "code": "",
                                "display": "",
                                "system": ""
                            }
                        ],
                        "text": ""
                    }
                ],
                "requester": {
                    "agent": {
                        "display": "Dimas, dr",
                        "reference": "Practitioner/0901R001-1196708-1-5712547b-d49e-4a43-b20a-3f704f07ebc6"
                    },
                    "onBehalfOf": {
                        "reference": "Organization/0901R001-1196708-1-d28a2a96-68af-4f94-a050-9d9c094b9066"
                    }
                },
                "meta": {
                    "lastUpdated": "2018-08-15 04:33:25"
                }
            },
            {
                "resourceType": "MedicationRequest",
                "text": {
                    "div": "ZINC TAB DISPERSIBLE 20 MG"
                },
                "identifier": {
				"system": "id_resep_pulang",
				"value": "0901R001-1196708-1-afb5967c-576c-4042-b412-770b21336557"
                },
                "subject": {
                    "display": "BASONI",
                    "reference": "Patient/0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c"
                },
                "intent": "final",
                "medicationCodeableConcept": {
                    "coding": [
                        {
                            "code": "DRx0017609",
                            "system": "http://rscm.co.id/drug"
                        }
                    ],
                    "text": "ZINC TAB DISPERSIBLE 20 MG"
                },
                "dosageInstruction": [
                    {
                        "doseQuantity": {
                            "code": "TAB",
                            "system": "http://unitsofmeasure.org",
                            "unit": "TAB",
                            "value": "2"
                        },
                        "route": {
                            "coding": [
                                {
                                    "code": "001",
                                    "display": "ORAL",
                                    "system": "http://snomed.info/sct"
                                }
                            ]
                        },
                        "timing": {
                            "repeat": {
                                "frequency": "1",
                                "period": 1,
                                "periodUnit": "d"
                            }
                        },
                        "additionalInstruction": [
                            {
                                "text": "1 kali per hari"
                            }
                        ]
                    }
                ],
                "reasonCode": [
                    {
                        "coding": [
                            {
                                "code": "",
                                "display": "",
                                "system": ""
                            }
                        ],
                        "text": ""
                    }
                ],
                "requester": {
                    "agent": {
                        "display": "Lully Kurniawan, drg",
                        "reference": "Practitioner/0901R001-1196708-1-8e4a4653-2565-4eab-9daa-2d9933a9d9ed"
                    },
                    "onBehalfOf": {
                        "reference": "Organization/0901R001-1196708-1-0a803fcc-22bd-40c3-b017-f797c288e96f"
                    }
                },
                "meta": {
                    "lastUpdated": "2018-08-18 07:13:42"
                }
            },
        ]
    }
