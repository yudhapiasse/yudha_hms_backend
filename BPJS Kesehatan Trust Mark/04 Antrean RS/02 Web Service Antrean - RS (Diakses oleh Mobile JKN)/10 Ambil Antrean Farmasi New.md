URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Mengambil antrean farmasi

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "kodebooking": "00012345678"
    }


Response

    {
        "response": {
            "jenisresep": "Racikan/Non Racikan",
            "nomorantrean": 1,
            "keterangan": ""
        },
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }
                 
      