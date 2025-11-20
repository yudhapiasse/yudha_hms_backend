{Base URL}/{Service Name}/Monitoring/Kunjungan/Tanggal/{Parameter 1}/JnsPelayanan/{Parameter 2}
Fungsi : Data Kunjungan

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Tanggal SEP format: yyyy-mm-dd

Parameter 2 : Jenis Pelayanan (1. Inap 2. Jalan)


        {
           "metaData": {
              "code": "200",
              "message": "Sukses"
           },
           "response": {
              "sep": [
                 {
                    "diagnosa": "K65.0",
                    "jnsPelayanan": "R.Inap",
                    "kelasRawat": "2",
                    "nama": "HANIF ABDURRAHMAN",
                    "noKartu": "0001819122189",
                    "noSep": "0301R00110170000004",
                    "noRujukan": "0301U01108180200084",
                    "poli": null,
                    "tglPlgSep": "2017-10-03",
                    "tglSep": "2017-10-01"
                 },
                 {
                    "diagnosa": "I50.0",
                    "jnsPelayanan": "R.Inap",
                    "kelasRawat": "3",
                    "nama": "ASRIZAL",
                    "noKartu": "0002283324674",
                    "noSep": "0301R00110170000005",
                    "noRujukan": "0301U01108180200184",
                    "poli": null,
                    "tglPlgSep": "2017-10-10",
                    "tglSep": "2017-10-01"
                 }
              ]
           }
        }
                                            
                                        