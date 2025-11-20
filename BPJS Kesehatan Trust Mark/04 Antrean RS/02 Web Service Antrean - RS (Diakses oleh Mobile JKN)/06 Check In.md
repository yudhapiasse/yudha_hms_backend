URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Memastikan pasien sudah datang di RS

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "kodebooking": "16032021A001",
        "waktu": 1616559330000
    }
    
    
    
    {
        "kodebooking": "{kodebooking yang didapat dari WS Ambil Antren}",
        "waktu": {waktu pasien checkin format timestamp dalam milisecond}
    }

Response:

    "metadata": {
        "code": 200,
        "message": "OK"
    }

Catatan:

Metadata code:
200: Sukses
201: Gagal
Selain metadata code 200, agar message pada metadata diisi sesuai dengan kondisi di lapangan.