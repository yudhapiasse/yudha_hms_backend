{BASE URL}/{Service Name}/antrean/batal
Fungsi : Membatalkan antrean pasien

Method : POST

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Request:

    {
        "kodebooking": "16032021A001",
        "keterangan": "Terjadi perubahan jadwal dokter, silahkan daftar kembali"
    }
    
    
    
    {
        "kodebooking": "{kodebooking yang didapat dari servis tambah antrean}",
        "keterangan": "{alasan pembatalan}"
    }   

Response:

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }