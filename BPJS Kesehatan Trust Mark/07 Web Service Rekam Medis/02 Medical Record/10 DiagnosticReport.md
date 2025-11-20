Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/DiagnosticReport.html

    {
        "resource": [
            {
                "resourceType": "DiagnosticReport",
                "id": "0901R001-1229344-2-acd3b089-2989-4bce-a4e8-fff504997d97",
                "subject": {
                    "reference": "Patient\/0901R001-1229344-2-6ef979ae-49f9-40e2-a359-d01880075a01",
                    "display": "WINO",
                    "noSep": "0901R0017718V045116"
                },
                "category": {
                    "coding": {
                        "system": "http:\/\/hl7.org\/fhir\/v2\/0074",
                        "code": "RAD",
                        "display": "Radiology"
                    }
                },
                "status": "final",
                "performer": [
                    {
                        "reference": "Organization\/0901R001-1229344-2-58238900-03d7-474e-b507-4c9b72d64a09",
                        "display": "Radiologi Dan Kedokteran Nuklir"
                    }
                ],
                "result": [
                    {
                        "resourceType": "Observation",
                        "id": "DX00150004994364",
                        "status": "final",
                        "text": {
                            "status": "generated",
                            "div": "Teknik: Radiografi toraks dalam proyeksi PA.((((((((Deskripsi:((((Jantung tidak membesar, cardiothoracic ratio <\/OBX.5.1.1>lt; 50%.<\/OBX.5.1.2><\/OBX.5.1>((((Aorta dan mediastinum superior tidak melebar.((((Trakea relatif di tengah. Kedua hilus tidak menebal.((((Corakan vaskular kedua paru masih baik. Tidak tampak infiltrat\/nodul. ((((Lengkung diafragma dan sinus kostofrenikus normal.((((Tulang-tulang yang tervisualisasi optimal kesan intak.((((((((((((<\/div>"
                        },
                        "issued": "2018-12-29 13:03:37",
                        "effectiveDateTime": "2018-12-29 12:34:11",
                        "code": {
                            "coding": {
                                "system": "http:\/\/snomed.info\/sct",
                                "code": "PROCx000025499",
                                "display": "THORAX"
                            },
                            "text": "THORAX"
                        },
                        "performer": {
                            "reference": "Practitioner\/0901R001-1229344-2-19ebfac8-4bc3-45f2-90ac-0acbf5c6d717",
                            "display": "dr. Benny Zulkarnaien, SpRad (K)."
                        },
                        "image": [
                            {
                                "comment": "",
                                "link": {
                                    "reference": "",
                                    "display": ""
                                }
                            }
                        ],
                        "conclusion": "Tak tampak kelainan radiologis pada jantung dan paru.(((("
                    }
                ]
            }
        ]
    }
