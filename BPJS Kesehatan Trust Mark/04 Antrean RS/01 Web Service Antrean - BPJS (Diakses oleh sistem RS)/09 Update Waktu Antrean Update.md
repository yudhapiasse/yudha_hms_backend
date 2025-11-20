{BASE URL}/{Service Name}/antrean/updatewaktu
Fungsi : Mengirimkan waktu tunggu/waktu layan

Method : POST

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Request
Response
Request

    {
        "kodebooking": "16032021A001",
        "taskid": 1,
        "waktu": 1616559330000,
        "jenisresep": "Tidak ada/Racikan/Non racikan" ---> khusus yang sudah implementasi antrean farmasi
    }
    
    
    
    {
    "kodebooking": "{kodebooking yang didapat dari servis tambah antrean}",
    "taskid": {
        1 (mulai waktu tunggu admisi),
        2 (akhir waktu tunggu admisi/mulai waktu layan admisi),
        3 (akhir waktu layan admisi/mulai waktu tunggu poli),
        4 (akhir waktu tunggu poli/mulai waktu layan poli),  
        5 (akhir waktu layan poli/mulai waktu tunggu farmasi),
        6 (akhir waktu tunggu farmasi/mulai waktu layan farmasi membuat obat),
        7 (akhir waktu obat selesai dibuat),
        99 (tidak hadir/batal)
    },
        "waktu": {waktu dalam timestamp milisecond}
    }                        

Response

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }

Catatan:

- Alur Task Id Pasien Baru: 1-2-3-4-5 (apabila ada obat tambah 6-7)
- Alur Task Id Pasien Lama: 3-4-5 (apabila ada obat tambah 6-7)
- Sisa antrean berkurang pada task 5
- Pemanggilan antrean poli pasien muncul pada task 4
- Cek in/mulai waktu tunggu untuk pasien baru mulai pada task 1
- Cek in/mulai waktu tunggu untuk pasien lama mulai pada task 3
- Agar terdapat validasi pada sistem RS agar alur pengiriman Task Id berurutan dari awal, dan waktu Task Id yang kecil
  lebih dulu daripada Task Id yang besar (misal task Id 1=08.00, task Id 2= 08.05)
- jenisresep : Tidak ada/Racikan/Non racikan (jenisresep khusus untuk rs yang sudah implementasi antrean farmasi. Jika
  belum/tidak kolom jenisresep dapat dihilangkan)