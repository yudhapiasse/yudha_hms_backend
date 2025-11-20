{BASE URL}/{Service Name}/ref/poli/fp
Fungsi : Melihat referensi poli finger print

Method : GET

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Respon : Perlu dilakukan dekripsi disisi client

    {
        "response": {
            "list": [{
                    "kodesubspesialis": "027",
                    "namasubspesialis": "Anak Alergi Imunologi",
                    "kodepoli": "ANA",
                    "namapoli": "ANAK"
                }
            ]
        },
        "metadata": {
        "message": "Ok",
        "code": 1
        }
    }                   
                                     
          