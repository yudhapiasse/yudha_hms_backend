{BASE URL}/{Service Name}/Sep/persetujuanSEP/list/bulan/{Parameter 1}/tahun/{Parameter 2}
Fungsi : Get List Data Persetujuan SEP

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Bulan (1-12)

Parameter 2: Tahun

    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "list": [
                {
                    "noKartu": "0002039003212",
                    "nama": "MARTA SENTANA",
                    "tglsep": "2021-11-23",
                    "jnspelayanan": "RJ",
                    "persetujuan": "Pengajuan",
                    "status": "Tgl.SEP Backdate"
                }
            ]
        }
    }
                                             
     