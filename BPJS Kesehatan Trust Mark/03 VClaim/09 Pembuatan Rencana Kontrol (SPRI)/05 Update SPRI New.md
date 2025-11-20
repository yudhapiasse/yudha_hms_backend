{BASE URL}/{Service Name}/RencanaKontrol/UpdateSPRI
Fungsi : Update SPRI

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
    "request":
        {
            "noSPRI":"0301R0110421K000116",
            "kodeDokter":"31537",
            "poliKontrol":"ANA",
            "tglRencanaKontrol":"2021-04-13",
            "user":"cobdda"
        }
}



        {
            "request":
                {
                    "noSPRI":"{nomor SPRI}",
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
                "tglRencanaKontrol": "2021-04-22",
                "namaDokter": "Dr.Yahya Marpaung,SpB, FINACS",
                "noKartu": "0001116500714",
                "nama": "M AMRU",
                "kelamin": "Laki-Laki",
                "tglLahir": "1997-12-16",
                "namaDiagnosa": null
            }
        }
             