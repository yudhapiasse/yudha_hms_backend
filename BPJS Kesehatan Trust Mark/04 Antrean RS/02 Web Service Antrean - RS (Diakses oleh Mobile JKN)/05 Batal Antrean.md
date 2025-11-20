{BASE URL}/antrean/batal
Fungsi : Membatalkan antrean pasien

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "kodebooking": "16032021A001",
        "keterangan": "Ada kebutuhan mendadak"
    }
    
    
    
    {
        "kodebooking": "{kodebooking yang didapat dari WS Ambil Antrean}",
        "keterangan": "{alasan pembatalan}"
    }

Response:

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }


Catatan:

- Format waktu dalam detik dengan formula: SPM * (sisa antrean-1)
- Metadata code:
  200: Sukses
  201: Gagal
  Selain metadata code 200, agar message pada metadata diisi sesuai dengan kondisi di lapangan