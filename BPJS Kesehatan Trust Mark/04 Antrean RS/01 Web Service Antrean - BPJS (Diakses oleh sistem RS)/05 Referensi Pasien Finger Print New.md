{BASE URL}/{Service Name}/ref/pasien/fp/identitas/{nik/noka}/noidentitas/{noidentitas}
Fungsi : Melihat referensi pasien finger print

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
            "nomorkartu": "0000000000031",
            "nik": "6748373747440003",
            "tgllahir": "2000-04-02",
            "daftarfp": 1
        },
        "metadata": {
        "message": "Ok",
        "code": 1
        }
    }                   
                                 
                                 