{BASE URL}/{Service Name}/antrean/add
Fungsi : Menambah Antrean RS

Method : POST

Format : Json

Request:

    {
        "kodebooking": "16032021A001",
        "jenispasien": "JKN",
        "nomorkartu": "00012345678",
        "nik": "3212345678987654",
        "nohp": "085635228888",
        "kodepoli": "ANA",
        "namapoli": "Anak",
        "pasienbaru": 0,
        "norm": "123345",
        "tanggalperiksa": "2021-01-28",
        "kodedokter": 12345,
        "namadokter": "Dr. Hendra",
        "jampraktek": "08:00-16:00",
        "jeniskunjungan": 1,
        "nomorreferensi": "0001R0040116A000001",
        "nomorantrean": "A-12",
        "angkaantrean": 12,
        "estimasidilayani": 1615869169000,
        "sisakuotajkn": 5,
        "kuotajkn": 30,
        "sisakuotanonjkn": 5,
        "kuotanonjkn": 30,
        "keterangan": "Peserta harap 30 menit lebih awal guna pencatatan administrasi."
    }
    
    
    
    {
        "kodebooking": "{kodebooking yang dibuat unik}",
        "jenispasien": "{JKN / NON JKN}",
        "nomorkartu": "{noka pasien BPJS,diisi kosong jika NON JKN}",
        "nik": "{nik pasien}",
        "nohp": "{no hp pasien}",
        "kodepoli": "{memakai kode subspesialis BPJS}",
        "namapoli": "{nama poli}",
        "pasienbaru": {1(Ya),0(Tidak)},
        "norm": "{no rekam medis pasien}",
        "tanggalperiksa": "{tanggal periksa}",
        "kodedokter": {kode dokter BPJS},
        "namadokter": "{nama dokter}",
        "jampraktek": "{jam praktek dokter}",
        "jeniskunjungan": {1 (Rujukan FKTP), 2 (Rujukan Internal), 3 (Kontrol), 4 (Rujukan Antar RS)},
        "nomorreferensi": "{norujukan/kontrol pasien JKN,diisi kosong jika NON JKN}",
        "nomorantrean": "{nomor antrean pasien}",
        "angkaantrean": {angka antrean},
        "estimasidilayani": {waktu estimasi dilayani dalam miliseconds},
        "sisakuotajkn": {sisa kuota JKN},
        "kuotajkn": {kuota JKN},
        "sisakuotanonjkn": {sisa kuota non JKN},
        "kuotanonjkn": {kuota non JKN},
        "keterangan": "{informasi untuk pasien}"
    }                        

Response:

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }
                        