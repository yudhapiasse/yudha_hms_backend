{BASE URL}/{Service Name}/SEP/2.0/updtglplg
Fungsi : Update tanggal pulang SEP 2.0

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

            {
                "request":{
                    "t_sep":{
                        "noSep": "0301R0110121V000829",
                        "statusPulang":"4",
                        "noSuratMeninggal":"325/K/KMT/X/2021",
                        "tglMeninggal":"2021-02-10",
                        "tglPulang":"2021-02-14",
                        "noLPManual":"",
                        "user":"coba"
                    }
                }
            }
                                     
                                     
                                                
            {
                "request":{
                    "t_sep":{
                        "noSep": "{nosep}",
                        "statusPulang":"{1:Atas Persetujuan Dokter, 3:Atas Permintaan Sendiri, 4:Meninggal, 5:Lain-lain}",
                        "noSuratMeninggal":"{diisi jika statusPulang 4, selain itu kosong}",
                        "tglMeninggal":"{diisi jika statusPulang 4, selain itu kosong. format yyyy-MM-dd}",
                        "tglPulang":"{format yyyy-MM-dd}",
                        "noLPManual":"{diisi jika SEPnya adalah KLL}",
                        "user":"{user}"
                    }
                }
            }


Response:

            {
                "metaData": {
                    "code": "200",
                    "message": "Ok"
                },
                "response": null
            }
                                     
                     