URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Menampilkan status antrean per poli (digunakan untuk perencanaan kedatangan pasien)

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "kodepoli": "ANA",
        "kodedokter": 12346,
        "tanggalperiksa": "2020-01-28",
        "jampraktek": "08:00-16:00"
    }
    
    
    
    {
        "kodepoli": "{memakai kode subspesialis BPJS}",
        "kodedokter": {kode dokter BPJS},
        "tanggalperiksa": "{tanggal rencana berobat}",
        "jampraktek": "{waktu praktek dokter yang diambil dari Aplikasi HFIS}"
    }                        

Response:

    {
        "response": {
            "namapoli": "Anak",
            "namadokter": "Dr. Hendra",
            "totalantrean": 25,
            "sisaantrean": 4,
            "antreanpanggil": "A-21",
            "sisakuotajkn": 5,
            "kuotajkn": 30,
            "sisakuotanonjkn": 5,
            "kuotanonjkn": 30,
            "keterangan": ""
        },
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }
         