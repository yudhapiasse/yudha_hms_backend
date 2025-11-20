{BASE URL}/{Service Name}/antrean/pendaftaran/kodebooking/{kodebooking}
Fungsi : Melihat pendaftaran antrean per kode booking

Method : GET

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Response
Respon : Perlu dilakukan dekripsi disisi client


    {
        "response": {
            "list": [
                {
                    "kodebooking": "ABC0000001",
                    "tanggal": "2021-03-24",
                    "kodepoli": "INT",
                    "kodedokter": 1234,
                    "jampraktek": "08:00-17:00",
                    "nik": "2749494383830001",
                    "nokapst": "0000000000013",
                    "nohp": "081234567890",
                    "norekammedis": "654321",
                    "jeniskunjungan": 1,
                    "nomorreferensi": "1029R0021221K000012",
                    "sumberdata": "Mobile JKN",
                    "ispeserta": 1,
                    "noantrean": "INT-0001",
                    "estimasidilayani": 1669278161000,
                    "createdtime": 1669278161000,
                    "status": "Selesai dilayani"
                }
            ]
        },
        "metadata": {
            "code": 200,
            "message": "OK"
        }
    }
          