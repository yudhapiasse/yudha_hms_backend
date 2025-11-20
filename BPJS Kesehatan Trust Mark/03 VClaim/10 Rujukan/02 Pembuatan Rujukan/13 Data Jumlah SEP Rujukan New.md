{BASE URL}/{Service Name}/Rujukan/JumlahSEP/{Parameter 1}/{Parameter 2}
Fungsi : Get Data Jumlah SEP yang terbentuk berdasarkan No Rujukan yang masuk ke RS

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Jenis Rujukan 1 -> fktp, 2 -> fkrtl

Parameter 2: No Rujukan



    {
        "metaData": {
            "code": "200",
            "message": "OK"
        },
        "response": {
            "jumlahSEP": "1"
        }
    }
       