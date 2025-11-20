{BASE URL}/{Service Name}/SEP/FingerPrint/randomanswer

Fungsi : POST Random Answer

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
        "request": {
            "t_sep": {
                "noKartu": "0002340532179",
                "tglSep": "2023-03-06",
                "jenPel":"1",
                "ppkPelSep": "0301R001",
                "tglLahir": "",
                "ppkPst": "09030300",
                "user": "user"
            }
        }
    }
    
    
    
    
    {
        "request": {
            "t_sep": {
                "noKartu": "{nomor kartu}",
                "tglSep": "{tanggal SEP}",
                "jenPel":"{jenis pelayanan}",
                "ppkPelSep": "{ppk pelayanan}",
                "tglLahir": "{tgl lahir}",
                "ppkPst": "{ppk peserta}",
                "user": "{user}"
            }
        }
    }
          