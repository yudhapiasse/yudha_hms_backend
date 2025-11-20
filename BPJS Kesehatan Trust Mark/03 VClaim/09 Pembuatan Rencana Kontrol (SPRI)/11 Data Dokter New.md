{BASE URL}/{Service Name}/RencanaKontrol/JadwalPraktekDokter/JnsKontrol/{parameter 1}/KdPoli/{parameter 2}/TglRencanaKontrol/{parameter 3}
Fungsi : Data Rencana Kontrol

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Jenis kontrol --> 1: SPRI, 2: Rencana Kontrol

Parameter 2: Kode poli

Parameter 3: Tanggal rencana kontrol --> format yyyy-MM-dd



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "list": [
                {
                    "kodeDokter": "31528",
                    "namaDokter": "Dr.John Wick",
                    "jadwalPraktek": "16:00 - 18:00",
                    "kapasitas": "12"
                },
                {
                    "kodeDokter": "31348",
                    "namaDokter": "Dr. Luffy",
                    "jadwalPraktek": "10:00 - 12:00",
                    "kapasitas": "12"
                }
            ]
        }
    }