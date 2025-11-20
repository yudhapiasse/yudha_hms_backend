{Base URL}/{Service Name}/referensi/pascapulang
Fungsi : Pencarian data pasca pulang

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8


        {
           "metaData": {
              "code": "200",
              "message": "Sukses"
           },
           "response": {
              "list": [
                 {
                    "kode": "1",
                    "nama": "Sembuh"
                 },
                 {
                    "kode": "2",
                    "nama": "Dirujuk"
                 },
                 {
                    "kode": "3",
                    "nama": "Pulang Paksa"
                 },
                 {
                    "kode": "4",
                    "nama": "Meninggal"
                 },
                 {
                    "kode": "5",
                    "nama": "Lain-Lain"
                 }
              ]
           }
        }
            