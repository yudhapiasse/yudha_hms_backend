{Base URL}/aplicaresws/rest/bed/create/{kodeppk}
Fungsi : Insert Ruangan Baru

Method : POST

Format : Json

Content-Type: application/json

Parameter :

kodekelas: kode kelas ruang rawat sesuai dengan mapping BPJS Kesehatan

koderuang: kode ruangan Rumah Sakit

namaruang: nama ruang rawat Rumah Sakit

kapasitas: Kapasitas ruang Rumah Sakit

tersedia: Jumlah tempat tidur yang kosong / dapat ditempati pasien baru

* Untuk Rumah Sakit yang ingin mencantumkan informasi ketersediaan tempat tidur untuk pasien laki – laki, perempuan, laki – laki atau perempuan

tersediapria : Jumlah tempat tidur yang kosong / dapat ditempati pasien baru laki – laki

Tersediawanita : Jumlah tempat tidur yang kosong / dapat ditempati pasien baru perempuan

tersediapriawanita : Jumlah tempat tidur yang kosong / dapat ditempati pasien baru laki – laki atau perempuan


    { 
        "kodekelas":"VIP",
        "koderuang":"RG01",
        "namaruang":"Ruang Anggrek VIP",
        "kapasitas":"20",
        "tersedia":"10",
        "tersediapria":"0",
        "tersediawanita":"0",
        "tersediapriawanita":"0"
    }
             
                                 
                                 