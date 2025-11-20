{BASE URL}/{Service Name}/RencanaKontrol/Update
Fungsi : Update Rencana Kontrol

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded


Request:

        {
            "request": {
                "noSuratKontrol":"0301R0110321K000002",
                "noSEP":"0301R0111018V000006",
                "kodeDokter":"11111",
                "poliKontrol":"INT",
                "tglRencanaKontrol":"2021-03-18",
                "user":"coba"
            }
        }
                                                
                                 
                                            
        {
            "request": {
                "noSuratKontrol":"{nomor surat kontrol}",
                "noSEP":"{nomor SEP}",
                "kodeDokter":"{kode dokter}",
                "poliKontrol":"{kode poli}",
                "tglRencanaKontrol":"{tanggal rencana kontrol, format: yyyy-MM-dd}",
                "user":"{user pembuat rencana kontrol}"
            }
        }


Response:

        {
            "metaData": {
                "code": "200",
                "message": "Ok"
            },
            "response": {
                "noSuratKontrol": "0301R0110520K000013",
                "tglRencanaKontrol": "2020-05-15",
                "namaDokter": "Dr. John Wick",
                "noKartu": "0001328186441",
                "nama": "ARIS",
                "kelamin": "Laki-laki",
                "tglLahir": "1947-12-31"
            }
        }
                                          
            