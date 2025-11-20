{Base URL}/{Service Name}/sjpresep/v3/insert
Fungsi : Simpan Resep

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:
        {
         "TGLSJP": "2021-08-05 18:13:11",
         "REFASALSJP": "1202R0010318V000092",
         "POLIRSP": "IPD",
         "KDJNSOBAT": "3", (1. Obat PRB, 2. Obat Kronis Blm Stabil, 3. Obat Kemoterapi)
         "NORESEP": "12346", 
         "IDUSERSJP": "USR-01",
         "TGLRSP": "2021-08-05 00:00:00", 
         "TGLPELRSP": "2021-08-05 00:00:00",
         "KdDokter": "0",
         "iterasi":"0" (0. Non Iterasi, 1. Iterasi)
        }                
                                     
                                     
                                                        
        {
         "TGLSJP": "2021-08-05 18:13:11",
         "REFASALSJP": "1202R0010318V000092",
         "POLIRSP": "IPD",
         "KDJNSOBAT": "3",
         "NORESEP": "12346", 
         "IDUSERSJP": "USR-01",
         "TGLRSP": "2021-08-05 00:00:00", 
         "TGLPELRSP": "2021-08-05 00:00:00",
         "KdDokter": "0",
         "iterasi":"0"
        }                



Response:

        {
          "response": {
            "noSep_Kunjungan": "1202R0010318V000092",
            "noKartu": "0000648450639",
            "nama": "SITI NAFISAH",
            "faskesAsal": "1202A002",
            "noApotik": "1202A00208210000001",
            "noResep": "12346",
            "tglResep": "2021-08-05",
            "kdJnsObat": "3",
            "byTagRsp": "0",
            "byVerRsp": "0",
            "tglEntry": "2021-08-05"
          },
          "metaData": {
            "code": "200",
            "message": "BERHASIL SIMPAN RESEP DENGAN NOSJP: 1202A00208210000001 No SEP RS sudah dilakukan iterasi sebanyak 1"
          }
        }
                                     
                    