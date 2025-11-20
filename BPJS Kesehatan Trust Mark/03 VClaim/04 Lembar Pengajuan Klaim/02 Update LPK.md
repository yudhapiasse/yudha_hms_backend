JSON
{BASE URL}/{Service Name}/LPK/update
Fungsi : Update Rujukan

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
           "request": {
              "t_lpk": {
                 "noSep": "0301R0011017V000015",
                 "tglMasuk": "2017-10-30",
                 "tglKeluar": "2017-10-30",
                 "jaminan": "1",
                 "poli": {
                    "poli": "INT"
                 },
                 "perawatan": {
                    "ruangRawat": "1",
                    "kelasRawat": "1",
                    "spesialistik": "1",
                    "caraKeluar": "1",
                    "kondisiPulang": "1"
                 },
                 "diagnosa": [
                    {
                       "kode": "N88.0",
                       "level": "1"
                    },
                    {
                       "kode": "A00.1",
                       "level": "2"
                    }
                 ],
                 "procedure": [
                    {
                       "kode": "00.82"
                    },
                    {
                       "kode": "00.83"
                    }
                 ],
                 "rencanaTL": {
                    "tindakLanjut": "1",
                    "dirujukKe": {
                       "kodePPK": ""
                    },
                    "kontrolKembali": {
                       "tglKontrol": "2017-11-10",
                       "poli": ""
                    }
                 },
                 "DPJP": "3",
                 "user": "Coba Ws"
              }
           }
        }               
                                     
                                     
                                                        
        {
           "request": {
              "t_lpk": {
                 "noSep": "{nomor sep}",
                 "tglMasuk": "{tanggal masuk format yyyy-mm-dd}",
                 "tglKeluar": "{tanggal keluar format yyyy-mm-dd}",
                 "jaminan": "{penjamin -> 1. JKN}",
                 "poli": {
                    "poli": "{kode poli -> data di referensi poli}"
                 },
                 "perawatan": {
                    "ruangRawat": "{ruang rawat -> data di referensi ruang rawat}",
                    "kelasRawat": "{kelas rawat -> data di referensi kelas rawat}",
                    "spesialistik": "{spesialistik -> data di referensi spesialistik}",
                    "caraKeluar": "{cara keluar -> data di referensi cara keluar}",
                    "kondisiPulang": "{kondisi pulang -> data di referensi kondisi pulang}"
                 },
                 "diagnosa": [
                    {
                       "kode": "{kode diagnosa  -> data di referensi diagnosa}",
                       "level": "{level diagnosa -> 1.Primer 2.Sekunder}"
                    },
                    {
                       "kode": "{kode diagnosa  -> data di referensi diagnosa}",
                       "level": "{level diagnosa -> 1.Primer 2.Sekunder}"
                    }
                 ],
                 "procedure": [
                    {
                       "kode": "{kode procedure -> data di referensi procedure/tindakan}"
                    },
                    {
                       "kode": "{kode procedure -> data di referensi procedure/tindakan}"
                    }
                 ],
                 "rencanaTL": {
                    "tindakLanjut": "{tindak lanjut -> 1:Diperbolehkan Pulang, 2:Pemeriksaan Penunjang, 3:Dirujuk Ke, 4:Kontrol Kembali}",
                    "dirujukKe": {
                       "kodePPK": "{kode faskes -> data di referensi faskes}"
                    },
                    "kontrolKembali": {
                       "tglKontrol": "{tanggal kontrol kembali format : yyyy-mm-dd}",
                       "poli": "{kode poli -> data di referensi poli}"
                    }
                 },
                 "DPJP": "{kode dokter dpjp -> data di referensi dokter}",
                 "user": "{user pemakai}"
              }
           }
        }                  


Response:

        {
           "metaData": {
              "code": "200",
              "message": "OK"
           },
           "response": "0301R0011017V000015"      
        }
                           