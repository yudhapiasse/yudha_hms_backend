URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Informasi jadwal operasi per pasien

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "nopeserta": "0000000000123"
    }
    
    
    
    {
        "nopeserta": "{no kartu pasien JKN}"
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
                "terlaksana": 0
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