{BASE URL}/{Service Name}/Rujukan/update
Fungsi : Update Rujukan

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
       "request": {
          "t_rujukan": {
             "noRujukan": "0301R0011117B000014",
             "ppkDirujuk": "0301R002",
             "tipe": "0",
             "jnsPelayanan": "1",
             "catatan": "test 3",
             "diagRujukan": "A00.1",
             "tipeRujukan": "1",
             "poliRujukan": "INT",
             "user": "Coba Ws"
          }
       }
    }             
                                 
                                 
                                                    
    {
       "request": {
          "t_rujukan": {
             "noRujukan": "{nomor rujukan}",
             "ppkDirujuk": "{faskes dirujuk -> data di referensi faskes}",
             "tipe": "{tipe rujukan -> 0.penuh, 1.Partial 2.rujuk balik}",
             "jnsPelayanan": "{jenis pelayanan -> 1.R.Inap 2.R.Jalan}",
             "catatan": "{catatan rujukan}",
             "diagRujukan": "{kode diagnosa rujukan -> data di referensi diagnosa}",
             "tipeRujukan": "{tipe rujukan -> 0.penuh, 1.Partial 2.rujuk balik}",
             "poliRujukan": "{kode poli rujukan -> data di referensi poli}",
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
       "response": 0301R0011117B000014 
    }

                                 
     