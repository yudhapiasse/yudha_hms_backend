{Base URL}/{Service Name}/referensi/obat/{Parameter 1}/{Parameter 2}/{Parameter 3}
Fungsi : Pencarian obat

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Kode Jenis Obat

Parameter 2 : Tgl Resep

Parameter 3 : Filter Pencarian


    {
            "response": {
                "list": [
                    {
                        "kode": "13210404174",
                        "nama": "Amlodipin 5 Temp tab 5 mg",
                        "harga": "75"
                    },
                    {
                        "kode": "13210404294",
                        "nama": "Amlodipin 10 Temp tab 10 mg",
                        "harga": "99"
                    }
                ]
            },
            "metaData": {
                "code": "200",
                "message": "OK"
            }
        }
	
