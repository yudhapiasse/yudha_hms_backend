Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Practitioner.html
    
    {
        "resource": {
            "resourceType": "Practitioner",
            "id": "0901R001-1196708-1-38b3196f-1adb-4336-af32-987290268ea7",
            "identifier": [
                {
                    "use": "official",
                    "system": "urn:oid:nomor_sip",
                    "value": "1.2.01.3173.1834/14022/04.16.1"
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
                    "value": "3172055103530001",
                    "assigner": {
                        "display": "KEMDAGRI"
                    }
                }
            ],
            "name": [
                {
                    "use": "official",
                    "text": "Suzanna Immanuel, Prof., Dr., dr., SpPK(K)"
                }
            ],
            "telecom": [
                {
                    "system": "phone",
                    "value": "0816-970-112",
                    "use": "work"
                },
                {
                    "system": "email",
                    "value": "suzanna.immanuel@gmail.com",
                    "use": "work"
                },
                {
                    "system": "fax",
                    "value": "",
                    "use": "work"
                },
                {
                    "system": "home",
                    "value": "",
                    "use": "home"
                }
            ],
            "address": [
                {
                    "use": "home",
                    "line": [
                        "Jl. Pasir Putih Vii/7 Rt.09/10 Ancol Kec.pademangan Jakut"
                    ],
                    "city": null,
                    "postalCode": "64714",
                    "country": null
                }
            ],
            "gender": "female",
            "birthDate": "1953-03-11 00:00:00"
        }
    }