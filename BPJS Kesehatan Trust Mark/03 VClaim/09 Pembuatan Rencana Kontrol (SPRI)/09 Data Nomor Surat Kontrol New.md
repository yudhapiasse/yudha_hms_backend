{BASE URL}/{Service Name}/RencanaKontrol/ListRencanaKontrol/tglAwal/{parameter 1}/tglAkhir/{parameter 2}/filter/{parameter 3}
Fungsi : Data Rencana Kontrol

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Tanggal awal format : yyyy-MM-dd

Parameter 2: Tanggal akhir format : yyyy-MM-dd

Parameter 3: Format filter --> 1: tanggal entri, 2: tanggal rencana kontrol



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "list": [
                {
                    "noSuratKontrol": "0301R0110321K000002",
                    "jnsPelayanan": "Rawat Jalan",
                    "jnsKontrol": "2",
                    "namaJnsKontrol": "Surat Kontrol",
                    "tglRencanaKontrol": "2021-03-18",
                    "tglTerbitKontrol": "2021-03-16",
                    "noSepAsalKontrol": "0301R0111018V000006",
                    "poliAsal": "INT",
                    "namaPoliAsal": "PENYAKIT DALAM",
                    "poliTujuan": "INT",
                    "namaPoliTujuan": "PENYAKIT DALAM",
                    "tglSEP": "2021-03-16",
                    "kodeDokter": "31479",
                    "namaDokter": "Prof.dr.Yulius,SpPD, KGEH",
                    "noKartu": "0001882053808",
                    "nama": "mela handayani"
                }
            ]
        }
    }
          