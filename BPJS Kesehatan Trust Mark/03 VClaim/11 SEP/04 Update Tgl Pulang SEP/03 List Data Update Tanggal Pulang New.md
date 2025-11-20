{BASE URL}/{Service Name}/Sep/updtglplg/list/bulan/{Parameter 1}/tahun/{Parameter 2}/{Parameter 3}
Fungsi : Get List Data Update Tanggal Pulang

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Bulan (1-12)

Parameter 2: Tahun

Parameter 3: Filter (Apabila dikosongkan akan menampilkan semua data pada bulan dan tahun pilihan)



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "list": [
                {
                    "noSep": "0138R0221221V000032",
                    "noSepUpdating": "0112R0761221V000014",
                    "jnsPelayanan": "1",
                    "ppkTujuan": "0138R022",
                    "noKartu": "0002047251712",
                    "nama": "SURIP",
                    "tglSep": "2021-12-13",
                    "tglPulang": "2021-12-15",
                    "status": "",
                    "tglMeninggal": "",
                    "noSurat": "",
                    "keterangan": "3.1.Peserta NoKa 0002047251712 telah mendapat Pelayanan R.Inap pada tgl. 13/12/2021 dan belum dipulangkan di RS CITRA MEDIKA DEPOK Dgn No.SEP 0138R0221221V000032",
                    "user": "AdminUtam"
                }
            ]
        }
    }
         