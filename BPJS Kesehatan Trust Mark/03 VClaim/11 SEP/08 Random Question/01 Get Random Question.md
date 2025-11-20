{BASE URL}/{Service Name}/SEP/FingerPrint/randomquestion/faskesterdaftar/nokapst/{parameter1}/tglsep/{parameter2}

Fungsi : Menampilkan Random Question

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter1: Nomor Kartu Peserta

Parameter2: Tanggal Pelayanan

    {
        "metaData": {
            "code": "200",
            "message": "Ok"
        },
        "response": {
            "faskes": [
                {
                    "kode": "0177B030",
                    "nama": "Klinik Citra Madina"
                },
                {
                    "kode": "21061801",
                    "nama": "DAMAU"
                },
                {
                    "kode": "01031201",
                    "nama": "PEUKAN BADA"
                }
            ]
        }
    }
    
            