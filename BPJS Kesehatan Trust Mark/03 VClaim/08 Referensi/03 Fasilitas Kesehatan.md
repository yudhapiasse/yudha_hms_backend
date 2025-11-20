{Base URL}/{Service Name}/referensi/faskes/{Parameter 1}/{Parameter 2}

Fungsi : Pencarian data fasilitas kesehatan

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : nama atau kode faskes

Parameter 2 : Jenis Faskes (1. Faskes 1, 2. Faskes 2/RS)


    {
        "metaData": 
            {
                "code": "200",
                "message": "Sukses"
            },
        "response": 
            {
            "faskes": 
                [
                    {
                        "kode": "00161001",
                        "nama": "PUSKESMAS SANGIRAN - KAB. SIMEULUE"
                    },
                    {
                        "kode": "00161002",
                        "nama": "PUSKESMAS SIMEULUE - KAB. SIMEULUE"
                    }
                ]
            }
    }
             
             