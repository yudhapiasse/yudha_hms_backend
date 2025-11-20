URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Informasi identitas pasien baru yang belum punya rekam medis (tidak ada norm di Aplikasi VClaim)

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:
    {
        "nomorkartu": "00012345678",
        "nik": "3212345678987654",
        "nomorkk": "3212345678987654",
        "nama": "sumarsono",
        "jeniskelamin": "L",
        "tanggallahir": "1985-03-01",
        "nohp": "085635228888",
        "alamat": "alamat yang muncul merupakan alamat lengkap",
        "kodeprop": "11",
        "namaprop": "Jawa Barat",
        "kodedati2": "0120",
        "namadati2": "Kab. Bandung",
        "kodekec": "1319",
        "namakec": "Soreang",
        "kodekel": "D2105",
        "namakel": "Cingcin",
        "rw": "001",
        "rt": "013"
    }
    
    
    
    {
        "nomorkartu": "{no kartu pasien JKN}",
        "nik": "{nika pasien}",
        "nomorkk": "{no kk pasien}",
        "nama": "{nama pasien}",
        "jeniskelamin": "{jenis kelamin pasien",
        "tanggallahir": "{tanggal lahir pasien}",
        "nohp": "{no hp pasien}",
        "alamat": "{alamat pasien}",
        "kodeprop": "{kode propinsi BPJS}",
        "namaprop": "{nama propinsi}",
        "kodedati2": "{kode kota/kab BPJS}",
        "namadati2": "{nama kota/kab}",
        "kodekec": "{kode kecamatan BPJS}",
        "namakec": "{nama kecamatan}",
        "kodekel": "{kode kelurahan BPJS}",
        "namakel": "{nama kelurahan}",
        "rw": "{no RT}",
        "rt": "{no RW}"
    }   

Response:


    {
        "response": {
            "norm": "123456"
        },
        "metadata": {
            "message": "Harap datang ke admisi untuk melengkapi data rekam medis",
            "code": 200
        }
    }


Catatan:

Metadata code:
200: Sukses
201: Gagal
Selain metadata code 200, agar message pada metadata diisi sesuai dengan kondisi di lapangan.
               