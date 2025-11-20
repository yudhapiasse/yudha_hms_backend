URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Melihat sisa antrean di hari H pelayanan

Method : POST

Format : Json

Header :
x-token: {token}
x-username: {user akses}

Request:

    {
        "kodebooking": "{kodebooking yang unik yang diambil dari WS Ambil Antrean}"
    }
    
    
    
    {
        "kodebooking": "16032021A001"
    }

Response

    {
        "response": {
            "nomorantrean": "A20",
            "namapoli": "Anak",
            "namadokter": "Dr. Hendra",
            "sisaantrean": 12,
            "antreanpanggil": "A-8",
            "waktutunggu": 9000,
            "keterangan": ""
        },
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
- Selain metadata code 200, agar message pada metadata diisi sesuai dengan kondisi di lapangan