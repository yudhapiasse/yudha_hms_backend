{BASE URL}/{Service Name}/jadwaldokter/kodepoli/{Parameter1}/tanggal/{Parameter2}
Fungsi : Melihat referensi jadwal dokter yang ada pada Aplikasi HFIS

Method : GET

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Parameter1 : {diisi kode poli BPJS}=> ANA

Parameter2 : {diisi tanggal}=> 2021-08-07

Respon : Perlu dilakukan dekripsi disisi client

    {
        "response": {
            "list": [{
                    "kodesubspesialis": "ANA",
                    "hari": 4,
                    "kapasitaspasien": 54,
                    "libur": 0,
                    "namahari": "KAMIS",
                    "jadwal": "08:00 - 12:00",
                    "namasubspesialis": "ANAK",
                    "namadokter": "DR. OKTORA WAHYU WIJAYANTO, SP.A",
                    "kodepoli": "ANA",
                    "namapoli": "Anak",
                    "kodedokter": 33690
                }, {
                    "kodesubspesialis": "ANA",
                    "hari": 4,
                    "kapasitaspasien": 20,
                    "libur": 0,
                    "namahari": "KAMIS",
                    "jadwal": "13:00 - 17:00",
                    "namasubspesialis": "ANAK",
                    "namadokter": "DR. OKTORA WAHYU WIJAYANTO, SP.A",
                    "kodepoli": "ANA",
                    "namapoli": "Anak",
                    "kodedokter": 33690
                }
            ]
        },
        "metadata": {
        "message": "Ok",
        "code": 200
        }
    }

Catatan:

hari = 1 (senin), 2 (selasa), 3 (rabu), 4 (kamis), 5 (jumat), 6 (sabtu), 7 (minggu), 8 (hari libur nasional).