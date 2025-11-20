{Base URL}/{Service Name}/riwayatobat/{parameter 1}/{parameter 2}/{parameter 3}
Fungsi : Riwayat Pelayanan Obat

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Tgl Awal

Parameter 2 : Tgl Akhir

Parameter 3 : NoKartu


    {
          "response": {
            "list": {
              "nokartu": "0000000000044",
              "namapeserta": "AGUSMA",
              "tgllhr": "1973-11-03",
              "history": [
                {
                  "nosjp": "1101A00309180000002",
                  "tglpelayanan": "2018-09-13",
                  "noresep": "00001",
                  "kodeobat": "12180400002",
                  "namaobat": "Akarbose 50 Dexa tab 50 mg",
                  "jmlobat": "46.00"
                },
                {
                  "nosjp": "1101A00309180000003",
                  "tglpelayanan": "2018-09-16",
                  "noresep": "00002",
                  "kodeobat": "12180401313",
                  "namaobat": "Triheksilfenidil 2 Mers tab 2 mg",
                  "jmlobat": "60.00"
                }
              ]
            }
          },
          "metaData": {
            "code": "200",
            "message": "Ok"
          }
        }
	
       