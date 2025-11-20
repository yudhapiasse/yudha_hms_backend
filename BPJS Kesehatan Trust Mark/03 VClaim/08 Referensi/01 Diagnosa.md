{Base URL}/{Service Name}/referensi/diagnosa/{parameter}

Fungsi : Pencarian data diagnosa (ICD-10)

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter : Kode atau Nama Diagnosa


    {
        "metaData": 
            {
                "code": "200",
                "message": "Sukses"
            },
        "response": 
            {
            "diagnosa": 
                [
                    {
                        "kode": "A04",
                        "nama": "A04 - Other bacterial intestinal infections"
                    }
                ],
            }
    }
          