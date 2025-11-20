Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Encounter.html

    {
        "resource": {
            "resourceType": "Encounter",
            "id": "0901R001-1196708-1-ef852407-45aa-43c7-b5e8-98d63b43c182",
            "identifier": [
                {
                    "system": "http://api.bpjs-kesehatan.go.id:8080/Vclaim-rest/SEP/",
                    "value": "0901R0022818V028012"
                }
            ],
            "subject": {
                "reference": "Patient/0901R001-1196708-1-af272919-8ed1-4aa2-8808-ece97328007c",
                "display": "BASONI",
                "noSep": "0901R0012218V028012"
            },
            "class": {
                "system": "http://hl7.org/fhir/v3/ActCode",
                "code": "IMP",
                "display": "inpatient encounter"
            },
            "incomingReferral": [
                {
                    "identifier": [
                        {
                            "system": "nomor_rujukan_bpjs",
                            "value": "diehr belum disimpan"
                        },
                        {
                            "system": "nomor_rujukan_internal_rs",
                            "value": "belum di buat"
                        }
                    ]
                }
            ],
            "reason": [
                {
                    "coding": [
                        {
                            "code": "",
                            "display": null,
                            "system": "http://hl7.org/fhir/sid/icd-10"
                        }
                    ],
                    "text": "LUKA BAKAR 52% TBSA"
                }
            ],
            "diagnosis": [
            
                {
                    "condition": {
                        "reference": "Condition/0901R001-1196708-1-f5fd08a5-a274-4c9e-a2bd-b7dc57fda0e2",
                        "role": {
                            "coding": [
                                {
                                    "system": "http://hl7.org/fhir/diagnosis-role",
                                    "code": "DD",
                                    "display": "Discharge Diagnosis"
                                }
                            ]
                        },
                        "rank": 1
                    }
                },
                {
                    "condition": {
                        "reference": "Condition/0901R001-1196708-1-a922a69a-a60e-448a-bbcc-b44e96b3dba7",
                        "role": {
                            "coding": [
                                {
                                    "system": "http://hl7.org/fhir/diagnosis-role",
                                    "code": "DD",
                                    "display": "Discharge Diagnosis"
                                }
                            ]
                        },
                        "rank": 2
                    }
                },
                {
                    "condition": {
                        "reference": "Condition/0901R001-1196708-1-dab83d12-b6dc-457c-8266-ecea7c099d7b",
                        "role": {
                            "coding": [
                                {
                                    "system": "http://hl7.org/fhir/diagnosis-role",
                                    "code": "DD",
                                    "display": "Discharge Diagnosis"
                                }
                            ]
                        },
                        "rank": 3
                    }
                },
                {
                    "condition": {
                        "reference": "Condition/0901R001-1196708-1-76615111-5141-4346-9c33-d601191b4700",
                        "role": {
                            "coding": [
                                {
                                    "system": "http://hl7.org/fhir/diagnosis-role",
                                    "code": "DD",
                                    "display": "Discharge Diagnosis"
                                }
                            ]
                        },
                        "rank": 4
                    }
                }
            ],
            "hospitalization": {
                "dischargeDisposition": [
                    {
                        "coding": [
                            {
                                "code": "home",
                                "display": "Home",
                                "system": "http://hl7.org/fhir/discharge-disposition"
                            }
                        ]
                    }
                ]
            },
            "period": {
                "end": "2018-09-13 18:11:00",
                "start": "2018-08-15 04:21:36"
            },
            "status": "finished",
            "text": {
                "div": "
    Admitted to Instalasi Gawat Darurat,Cipto Mangunkusumo Hospital between 15 Agustus 2018 04:21 and 13 September 2018 18:11
    ",
            "status": "generated"
            }
        }
    }