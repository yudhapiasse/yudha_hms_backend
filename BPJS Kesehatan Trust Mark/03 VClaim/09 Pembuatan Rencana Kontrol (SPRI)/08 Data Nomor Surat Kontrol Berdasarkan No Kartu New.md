{BASE URL}/{Service Name}/RencanaKontrol/ListRencanaKontrol/Bulan/{parameter 1}/Tahun/{parameter 2}/Nokartu/{parameter 3}/filter/{parameter 4}
Fungsi : Data Rencana Kontrol By No Kartu

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Bulan. Contoh: Januari => 01

Parameter 2: Tahun

Parameter 3: Nomor Kartu

Parameter 4: Format filter --> 1: tanggal entri, 2: tanggal rencana kontrol



    {
        "metaData":{
        "code":"200",
        "message":"Sukses"
        },
        "response":{
            "list":[
                {
                    "noSuratKontrol":"0117R0770122K000004",
                    "jnsPelayanan":"Rawat Inap",
                    "jnsKontrol":"2",
                    "namaJnsKontrol":"Surat Kontrol",
                    "tglRencanaKontrol":"2022-01-06",
                    "tglTerbitKontrol":"2022-01-05",
                    "noSepAsalKontrol":"0117R0770122V000003",
                    "poliAsal":"INT",
                    "namaPoliAsal":"-",
                    "poliTujuan":"INT",
                    "namaPoliTujuan":"PENYAKIT DALAM",
                    "tglSEP":"2022-01-04",
                    "kodeDokter":"296676",
                    "namaDokter":"ABD KADIR",
                    "noKartu":"0002035874204",
                    "nama":"ANI AZKIA",
                    "terbitSEP":"Belum"
                }
            ]
        }
    }