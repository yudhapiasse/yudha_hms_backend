{BASE URL}/{Service Name}/SEP/FingerPrint/Peserta/{parameter1}/TglPelayanan/{parameter2}

Fungsi : Pencarian data fingerprint

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter1: Nomor Kartu Peserta

Parameter2: Tanggal Pelayanan

Jika telah dilakukan validasi fingerprint:

    {
        "metaData": {
            "code": "200",
            "message": "Ok"
        },
        "response": {
            "kode": "1",
            "status": "Peserta telah melakukan validasi finger print pada tanggal 2020-01-21"
        }
    }

Jika belum dilakukan validasi fingerprint:

    {
        "metaData": {
            "code": "200",
            "message": "Ok"
        },
        "response": {
            "kode": "0",
            "status": "Peserta belum melakukan validasi finger print"
        }
    }
