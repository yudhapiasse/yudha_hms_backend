{Base URL}/{Service Name}/monitoring/JasaRaharja/JnsPelayanan/{Parameter 1}/tglMulai/{Parameter 2}/tglAkhir/{Parameter 3}
Fungsi : Monitoring Klaim Jasa Raharja

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Jenis Pelayanan (1. Rawat Inap, 2. Rawat Jalan)

Parameter 2 : Tgl Mulai Pencarian (yyyy-mmdd)

Parameter 3 : Tgl Akhir Pencarian (yyyy-mmdd)


    {
       "metaData": {
          "code": "200",
          "message": "Sukses"
       },
       "response": {
          "jaminan": [
             {
                "sep": 
                {
                    "noSEP":"0301R0110818V100085",
                    "tglSEP":"2018-08-09",
                    "tglPlgSEP":"2018-08-09",
                    "noMr":"AA-01-11",
                    "jnsPelayanan":"2",
                    "poli":"INT",
                    "diagnosa":"A00.1",
                    "peserta":
                    {
                        "noKartu":"0001161271256",
                        "nama":"JASA RAHARJA",
                        "noMR":"AA-01-11"
                    }
                },
                "jasaRaharja":
                {
                    "tglKejadian":"2018-08-09",
                    "noRegister":"AA-JR-0801",
                    "ketStatusDijamin":"Dijamin",
                    "ketStatusDikirim":"Sukses",
                    "biayaDijamin":"100000",
                    "plafon":"20000000",
                    "jmlDibayar":"10000",
                    "resultsJasaRaharja":"Sukses"
                }
             },
             {
                "sep": 
                {
                    "noSEP":"0301R0110818V100185",
                    "tglSEP":"2018-08-09",
                    "tglPlgSEP":"2018-08-09",
                    "noMr":"AA-01-11",
                    "jnsPelayanan":"2",
                    "poli":"INT",
                    "diagnosa":"A00.1",
                    "peserta":
                    {
                        "noKartu":"0003361271256",
                        "nama":"JASA RAHARJA",
                        "noMR":"AA-01-11"
                    }
                },
                "jasaRaharja":
                {
                    "tglKejadian":"2018-08-09",
                    "noRegister":"AA-JR-0801",
                    "ketStatusDijamin":"Dijamin",
                    "ketStatusDikirim":"Sukses",
                    "biayaDijamin":"100000",
                    "plafon":"20000000",
                    "jmlDibayar":"10000",
                    "resultsJasaRaharja":"Sukses"
                }
             }
          ]
       }
    }
      