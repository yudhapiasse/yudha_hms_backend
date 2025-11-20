{Base URL}/{Service Name}/referensi/dokter/pelayanan/{Parameter 1}/tglPelayanan/{Parameter 2}/Spesialis/{Parameter 3}

Fungsi : Pencarian data dokter DPJP untuk pengisian DPJP Layan

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Jenis Pelayanan (1. Rawat Inap, 2. Rawat Jalan)

Parameter 2 : Tgl.Pelayanan/SEP (yyyy-mm-dd)

Parameter 3 : Kode Spesialis/Subspesialis


        {
           "metaData":{
              "code":"200",
              "message":"Sukses"
           },
           "response":{
              "list":[
                 {
                    "kode":"31486",
                    "nama":"Satro Jadhit, dr"
                 },
                 {
                    "kode":"31492",
                    "nama":"Satroni Lawa, dr"
                 }
              ]
           }
        }
               