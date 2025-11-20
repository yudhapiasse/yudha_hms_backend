{BASE URL}/{Service Name}/jadwaldokter/updatejadwaldokter
Fungsi : Update jadwal dokter yang ada pada Aplikasi HFIS

Method : POST

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Request:

    {
    "kodepoli": "ANA",
    "kodesubspesialis": "ANA",
    "kodedokter": 12346,
        "jadwal": [
            {
                "hari": "1",
                "buka": "08:00",
                "tutup": "10:00"
            },
            {
                "hari": "2",
                "buka": "15:00",
                "tutup": "17:00"
            }
        ]
    }

    
    
    {
    "kodepoli": "{kode poli BPJS}",
    "kodesubspesialis": "{kode subspesialis BPJS}",
    "kodedokter": {kode dokter BPJS},
        "jadwal": [
            {
                "hari": "{1 (senin), 2 (selasa), 3 (rabu), 4 (kamis), 5 (jumat), 6 (sabtu), 7 (minggu), 8 (hari libur nasional)}",
                "buka": "{waktu}",
                "tutup": "{waktu}"
            },
            {
                "hari": "{1 (senin), 2 (selasa), 3 (rabu), 4 (kamis), 5 (jumat), 6 (sabtu), 7 (minggu), 8 (hari libur nasional)}",
                "buka": "{waktu}",
                "tutup": "{waktu}"
            }
        ]
    }    

Response:

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }


Catatan:

- Data yang berhasil disimpan menunggu aproval dari BPJS atau otomatis approve jadwal dokter oleh sistem, misal pengajuan perubahan jadwal oleh RS diantara jam 00.00 - 20.00 , kemudian alokasi approve manual oleh BPJS/cabang di jam 20.01-00.00. Jika pukul 00.00 belum dilakukan aproval oleh kantor cabang, maka otomatis approve by sistem akan dilaksanakan setelah jam 00.00 dan yang berubahnya esoknya (H+1).