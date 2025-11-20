{BASE URL}/{Service Name}/RencanaKontrol/ListSpesialistik/JnsKontrol/{parameter 1}/nomor/{parameter 2}/TglRencanaKontrol/{parameter 3}
Fungsi : Data Rencana Kontrol

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Jenis kontrol --> 1: SPRI, 2: Rencana Kontrol

Parameter 2: Nomor --> jika jenis kontrol = 1, maka diisi nomor kartu; jika jenis kontrol = 2, maka diisi nomor SEP

Parameter 3: Tanggal rencana kontrol --> format yyyy-MM-dd



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "list": [
                {
                    "kodePoli": "004",
                    "namaPoli": "Alergi-Immunologi Klinik ",
                    "kapasitas": "30",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                },
                {
                    "kodePoli": "005",
                    "namaPoli": "Gastroenterologi-Hepatologi ",
                    "kapasitas": "12",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                },
                {
                    "kodePoli": "008",
                    "namaPoli": "Hematologi - Onkologi Medik ",
                    "kapasitas": "24",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                },
                {
                    "kodePoli": "013",
                    "namaPoli": "Reumatologi ",
                    "kapasitas": "24",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                },
                {
                    "kodePoli": "015",
                    "namaPoli": "Kardiovaskular ",
                    "kapasitas": "24",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                },
                {
                    "kodePoli": "023",
                    "namaPoli": "obstetri ginekologi sosial",
                    "kapasitas": "12",
                    "jmlRencanaKontroldanRujukan": "0",
                    "persentase": "0.00"
                }
            ]
        }
    }
       