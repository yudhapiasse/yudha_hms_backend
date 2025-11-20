URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Informasi jadwal operasi di rumah sakit

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        `"tanggalawal": "2019-12-11",
        "tanggalakhir": "2019-12-13"`
    }
    
    
    
    {
        "tanggalawal": "{tanggal awal pencarian}",
        "tanggalakhir": "{tanggal akhir pencarian}"
    }

Response:


    {
        "response": {
            "list" : [{
                "kodebooking": "123456ZXC",
                "tanggaloperasi": "2019-12-11",
                "jenistindakan": "operasi gigi",
                "kodepoli": "001",
                "namapoli": "Poli Bedah Mulut",
                "terlaksana": 1,
                "nopeserta": "0000000924782",
                "lastupdate": 1577417743000
            },
            {
                "kodebooking": "67890QWE",
                "tanggaloperasi": "2019-12-11",
                "jenistindakan": "operasi mulut",
                "kodepoli": "001",
                "namapoli": "Poli Bedah Mulut",
                "terlaksana": 0,
                "nopeserta": "",
                "lastupdate": 1577417743000
            }]
        },
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }


Catatan:

- Kode poli memakai kode subspesialis BPJS
- Metadata code:
  200: Sukses
  201: Gagal
  Selain metadata code 200, agar message pada metadata diisi sesuai dengan kondisi di lapangan.