JSON
{BASE URL}/{Service Name}/RencanaKontrol/insert
Fungsi : Insert Rencana Kontrol

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded


Request:

        {
            "request": {
                "noSEP":"0301R0111018V000006",
                "kodeDokter":"12345",
                "poliKontrol":"INT",
                "tglRencanaKontrol":"2021-03-20",
                "user":"ws"
            }
        }
                                                        
                                 
                                                    
        {
            "request": {
                "noSEP":"{nomor SEP}",
                "kodeDokter":"{kode dokter}",
                "poliKontrol":"{kode poli}",
                "tglRencanaKontrol":"{Rawat Jalan: diisi tanggal rencana kontrol, format: yyyy-MM-dd. Rawat Inap: diisi tanggal SPRI, format: yyyy-MM-dd}",
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
             