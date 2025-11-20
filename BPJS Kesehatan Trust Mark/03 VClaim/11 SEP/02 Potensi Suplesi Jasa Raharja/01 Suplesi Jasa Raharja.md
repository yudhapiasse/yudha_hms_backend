{Base URL}/{Service Name}/sep/JasaRaharja/Suplesi/{Parameter 1}/tglPelayanan/{Parameter 2}
Fungsi : Pencarian data potensi SEP Sebagai Suplesi Jasa Raharja

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : No.Kartu Peserta

Parameter 2 : Tgl.Pelayanan/SEP (yyyy-mm-dd)


        {
            "metaData": 
                {
                    "code": "200",
                    "message": "Sukses"
                },
            "response": 
                {
                "jaminan": 
                    [
                        {
                            "noRegister": "1234",
                            "noSep": "0301R0110818V000008",
                            "noSepAwal": "0301R0110818V000008",
                            "noSuratJaminan": "-",
                            "tglKejadian": "2018-08-06",
                            "tglSep": "2018-08-08"                                    
                        },
                                        {
                            "noRegister": "44222",
                            "noSep": "0301R0110818V000018",
                            "noSepAwal": "0301R0110818V000008",
                            "noSuratJaminan": "-",
                            "tglKejadian": "2018-08-06",
                            "tglSep": "2018-08-08"                                    
                        }    
                    ],
                }
        }
            