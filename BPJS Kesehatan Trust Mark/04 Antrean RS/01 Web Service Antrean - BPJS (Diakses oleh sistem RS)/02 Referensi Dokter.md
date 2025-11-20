{BASE URL}/{Service Name}/ref/dokter
Fungsi : Melihat referensi dokter yang ada pada Aplikasi HFIS

Method : GET

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Respon : Perlu dilakukan dekripsi disisi client

    {
        "metadata": {
        "code": 1,
        "message": "OK"
        },
        "response": {
            "list": [
                {
                    "namadokter": "drg. Kusumawati Sukadi, Sp.BM",
                    "kodedokter": 700
                },
                {
                    "namadokter": "Dr. Dr. Noer Rachma, Sp.KFR",
                    "kodedokter": 854
                }
            ]
        }
    }  