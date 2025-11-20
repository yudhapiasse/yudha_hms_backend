Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/composition.html


    {
            "resource": {
                "resourceType": "Composition",
                "id": "0901R001-1196708-1-e4d2ab1d-cb72-484d-87cb-5616882657d9",
                "status": "final",
                "type": {
                    "coding": [
                            {
                                "system": "http://loinc.org",
                                "code": "81218-0"
                            }
                        ],
                        "text": "Discharge Summary"
                        },
                        "subject": {
                            "reference": "Patient/0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c",
                            "display": "BASNI"
                        },
                        "encounter": {
                            "reference": "Encounter/0901R001-1196708-1-ef852407-45aa-43c7-b5e8-98d63b43c182"
                        },
                        "date": "2018-08-15 04:21:36",
                            "author": [
                                {
                                    "reference": "Practitioner/0901R001-1196708-1-756a608c-2c33-4199-a773-63df905ac315",
                                    "display": "R. Aditya Wardhana, dr., SpBP-RE(K)"
                                }
                            ],
                        "title": "Discharge Summary",
                        "confidentiality": "N",
                        "section": {
                        "0": {
                        "title": "Reason for admission",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "29299-5",
                                    "display": "Reason for visit Narrative"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            "
                        }
                        },
                        "1": {
                            "title": "Chief complaint",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "10154-3",
                                    "display": "Chief complaint Narrative"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            "
                        }
                        },
                        "2": {
                        "title": "Admission diagnosis",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "42347-5",
                                    "display": "Admission diagnosis Narrative"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            LUKA BAKAR 52% TBSA,
                            "
                        },
                            "entry": [
                                {
                                    "reference": "urn:uuid:541a72a8-df75-4484-ac89-ac4923f03b81"
                                }
                            ]
                        },
                        "4": {
                        "title": "Medications on Discharge",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "75311-1",
                                    "display": "Hospital discharge medications Narrative"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            (CLINDAMYCIN)CLINDAMYCIN CAPSULE 300 MG 10 CAP,
                            "
                        },
                        "mode": "working",
                            "entry": [
                                {
                                    "reference": "MedicationRequest/0901R001-1196708-1-ef852407-45aa-43c7-b5e8-98d63b43c182"
                                },
                                {
                                    "reference": "MedicationRequest/0901R001-1196708-1-ef852407-45aa-43c7-b5e8-98d63b43c182"
                                }
                            ]
                        },
                        "5": {
                        "title": "Plan of care",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "18776-5",
                                    "display": "Plan of care"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            "
                        },
                        "mode": "working",
                            "entry": [
                                {
                                    "reference": "MedicationRequest/124a6916-5d84-4b8c-b250-10cefb8e6e86"
                                }
                            ]
                        },
                        "7": {
                        "title": "Known allergies",
                        "code": {
                            "coding": [
                                {
                                    "system": "http://loinc.org",
                                    "code": "48765-2",
                                    "display": "Allergies and adverse reactions"
                                }
                            ]
                        },
                        "text": {
                            "status": "additional",
                            "div": "
                            "
                        },
                        "entry": [
                        {
                            "reference": "AllergyIntolerance/47600e0f-b6b5-4308-84b5-5dec157f7637"
                        }
                    ]
                }
            }
        }
    }                 
                                     
                                 