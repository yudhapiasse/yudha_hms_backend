{Base URL}/{Service Name}/monitoring/HistoriPelayanan/NoKartu/{Parameter 1}/tglMulai/{Parameter 2}/tglAkhir/{Parameter 3}

Fungsi : Histori Pelayanan Per Peserta

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : No.Kartu Peserta

Parameter 2 : Tgl Mulai Pencarian (yyyy-mmdd)

Parameter 3 : Tgl Akhir Pencarian (yyyy-mmdd)


    {
       "metaData": {
          "code": "200",
          "message": "Sukses"
       },
       "response": {
          "histori": [
             {
                "diagnosa": "A00.1 - Cholera due to Vibrio cholerae 01, biovar eltor",
                "jnsPelayanan": "1",
                "kelasRawat": "Kelas 1",
                "namaPeserta": "STAMI",
                "noKartu": "0001160271256",
                "noSep": "0301R0110818V200084",
                "noRujukan": "0301U01108180200084",
                "poli": "",
                "ppkPelayanan": "RS YOS SUDARSO",
                "tglPlgSep": "2018-07-11",
                "tglSep": "2018-07-09"
             },
             {
                "diagnosa": "A00.1 - Cholera due to Vibrio cholerae 01, biovar eltor",
                "jnsPelayanan": "2",
                "kelasRawat": null,
                "namaPeserta": "STAMI",
                "noKartu": "0001160271256",
                "noSep": "0301R0110818V100085",
                "noRujukan": "0301U01108180201084",
                "poli": "",
                "ppkPelayanan": "RS YOS SUDARSO",
                "tglPlgSep": "2018-08-09",
                "tglSep": "2018-08-09"
             }
          ]
       }
    }
                                            
          