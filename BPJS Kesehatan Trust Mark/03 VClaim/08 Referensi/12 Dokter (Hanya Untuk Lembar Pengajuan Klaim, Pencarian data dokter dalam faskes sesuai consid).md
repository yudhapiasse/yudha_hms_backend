{Base URL}/{Service Name}/referensi/dokter/{Parameter}
Fungsi : Pencarian data dokter dalam faskes sesuai consid

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter : nama dokter/DPJP


        {
           "metaData":{
              "code":"200",
              "message":"Sukses"
           },
           "response":{
              "list":[
                 {
                    "kode":"3",
                    "nama":"Satro Jadhit, dr"
                 },
                 {
                    "kode":"2",
                    "nama":"Satroni Lawa, dr"
                 }
              ]
           }
        }
           