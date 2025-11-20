Format : Json

Content-Type: application/json; charset=utf-8

Sumber : https://www.hl7.org/fhir/Organization.html

    {
        "resource": {
            "resourceType": "Organization",
            "id": "0901R001-1196708-1-672ca120-e0b4-449a-be3a-10a63c3c5519",
            "identifier": [
                {
                    "use": "official",
                    "system": "urn:oid:bpjs",
                    "value": "0901R001"
                },
                {
                    "use": "official",
                    "system": "urn:oid:kemkes",
                    "value": "3173014"
                }
            ],
            "type": [
                {
                    "coding": [
                        {
                            "system": "http://hl7.org/fhir/organization-type",
                            "code": "prov",
                            "display": "Healthcare Provider"
                        }
                    ]
                }
            ],
            "name": "IGD - Radiologi",
            "alias": [
                "RSCM"
            ],
            "telecom": [
                {
                    "system": "phone",
                    "value": "1500-135",
                    "use": "work"
                }
            ],
            "address": [
                {
                    "use": "work",
                    "text": "Jl. Pangeran Diponegoro No. 71, Kenari, Senen, RW. 5, Kenari, RW.5, Kenari, Senen, Jakarta Pusat, Daerah Khusus Ibukota Jakarta, 10430, Indonesia",
                    "line": [
                        "Jl. Pangeran Diponegoro No. 71, Kenari, Senen, RW. 5, Kenari, RW.5, Kenari, Senen"
                    ],
                    "city": "Jakarta Pusat",
                    "state": "Daerah Khusus Ibukota Jakarta",
                    "postalCode": "10430",
                    "country": "IDN"
                }
            ],
            "contact": [
                {
                    "purpose": {
                        "coding": [
                            {
                                "system": "http://hl7.org/fhir/contactentity-type",
                                "code": "PATINF"
                            }
                        ]
                    },
                    "telecom": [
                        {
                            "system": "phone",
                            "value": "1500-135"
                        }
                    ]
                }
            ]
        }
    }