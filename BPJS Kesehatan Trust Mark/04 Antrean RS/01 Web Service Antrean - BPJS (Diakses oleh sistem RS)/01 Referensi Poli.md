{BASE URL}/{Service Name}/ref/poli
Fungsi : Melihat referensi poli yang ada pada Aplikasi HFIS

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
                    "nmpoli": "AKUPUNTUR MEDIK",
                    "nmsubspesialis": "AKUPUNTUR MEDIK",
                    "kdsubspesialis": "AKP",
                    "kdpoli": "AKP"
                },
                {
                    "nmpoli": "ANAK",
                    "nmsubspesialis": "ANAK ALERGI IMUNOLOGI",
                    "kdsubspesialis": "027",
                    "kdpoli": "ANA"
                }
            ]
        }
    }                    
                                 
                      