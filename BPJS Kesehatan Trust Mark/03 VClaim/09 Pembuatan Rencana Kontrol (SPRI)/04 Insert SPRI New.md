{BASE URL}/{Service Name}/RencanaKontrol/InsertSPRI
Fungsi : Insert SPRI

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded


Request:

        {
            "request":
                {
                    "noKartu":"0001116500714",
                    "kodeDokter":"31537",
                    "poliKontrol":"BED",
                    "tglRencanaKontrol":"2021-04-13",
                    "user":"sss"
                }
        }
                                                
                                 
                                            
        {
            "request":
                {
                    "noKartu":"{nomor Kartu}",
                    "kodeDokter":"{kode dokter}",
                    "poliKontrol":"{poli kontrol}",
                    "tglRencanaKontrol":"{tgl rencana kontrol, format:yyyy-MM-dd}",
                    "user":"{user pembuat spri}"
                }
        }


Response:

        {
            "metaData": {
                "code": "200",
                "message": "Ok"
            },
            "response": {
                "noSPRI": "0301R0110421K000002",
                "tglRencanaKontrol": "2021-04-20",
                "namaDokter": "Dr.Yahya Marpaung,SpB, FINACS",
                "noKartu": "0001116500714",
                "nama": "M AMRU",
                "kelamin": "Laki-Laki",
                "tglLahir": "1997-12-16",
                "namaDiagnosa": null
            }
        }
                                            
             