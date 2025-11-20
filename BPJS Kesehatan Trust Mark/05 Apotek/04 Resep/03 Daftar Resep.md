{Base URL}/{Service Name}/daftarresep
Fungsi : Daftar Resep

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
            "kdppk": "0112A017",
            "KdJnsObat": "0",
            "JnsTgl": "TGLPELSJP", format -> TGLPELSJP,TGLRSP
            "TglMulai": "2019-03-01 08:49:45",
            "TglAkhir": "2019-03-31 06:18:33"
        }                
                                     
                                     
                                                        
        {
            "kdppk": "0112A017",
            "KdJnsObat": "0",
            "JnsTgl": "TGLPELSJP",
            "TglMulai": "2019-03-01 08:49:45",
            "TglAkhir": "2019-03-31 06:18:33"
        }                


Response:

        {
            "metaData":
            {
                "code":"200",
                "message":"Ok."
            },
            "response":
            {
                "resep":
                    {
                        "NORESEP":"01236",
                        "NOAPOTIK":"0112A01704190000001",
                        "NOSEP_KUNJUNGAN":"0112R0340418V004961",
                        "NOKARTU":"0002338679259",
                        "NAMA":"SITI SULASTRI",
                        "TGLENTRY":"2019-04-02 11:13:33.000+07:00",
                        "TGLRESEP":"2019-03-19 00:00:00.000+07:00",
                        "TGLPELRSP":"2019-03-26 00:00:00.000+07:00",
                        "BYTAGRSP":"0.00",
                        "BYVERRSP":"0.00",
                        "KDJNSOBAT":"2",
                        "FASKESASAL":"0112R034"
                    }
            }
        }    