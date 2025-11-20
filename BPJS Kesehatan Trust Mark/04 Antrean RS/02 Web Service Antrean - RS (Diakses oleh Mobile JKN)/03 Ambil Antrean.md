URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Mengambil antrean

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "nomorkartu": "00012345678",
        "nik": "3212345678987654",
        "nohp": "085635228888",
        "kodepoli": "ANA",
        "norm": "123345",
        "tanggalperiksa": "2021-01-28",
        "kodedokter": 12345,
        "jampraktek": "08:00-16:00",
        "jeniskunjungan": 1,
        "nomorreferensi": "0001R0040116A000001"
    }
    
    
    
    {
        "nomorkartu": "{noka pasien BPJS,diisi kosong jika NON JKN}",
        "nik": "{nika pasien}",
        "nohp": "{no hp pasien}",
        "kodepoli": "{memakai kode subspesialis BPJS}",
        "norm": "{no rekam medis pasien}",
        "tanggalperiksa": "{tanggal periksa}",
        "kodedokter": {kode dokter BPJS},
        "jampraktek": "{jam praktek dokter}",
        "jeniskunjungan": {1 (Rujukan FKTP), 2 (Rujukan Internal), 3 (Kontrol), 4 (Rujukan Antar RS)},
        "nomorreferensi": "{norujukan/kontrol pasien JKN,diisi kosong jika NON JKN}"
    }                        



Response:
    
    {
        "response": {
            "nomorantrean": "A-12",
            "angkaantrean": 12,
            "kodebooking": "16032021A001",
            "norm": "123345",
            "namapoli": "Anak",
            "namadokter": "Dr. Hendra",
            "estimasidilayani": 1615869169000,
            "sisakuotajkn": 5,
            "kuotajkn": 30,
            "sisakuotanonjkn": 5,
            "kuotanonjkn": 30,
            "keterangan": "Peserta harap 60 menit lebih awal guna pencatatan administrasi."
        },
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }


Catatan:

estimasidilayani : format dalam milisecond

Metadata code:
200: Sukses
201: Gagal
202: Pasien Baru
Ketika RS merespon code 202, mobile JKN akan mengirimkan data pasien baru (hit WS Info Pasien Baru).    