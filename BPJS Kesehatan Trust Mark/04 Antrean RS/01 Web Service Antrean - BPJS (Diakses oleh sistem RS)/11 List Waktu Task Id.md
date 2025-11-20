{BASE URL}/{Service Name}/antrean/getlisttask
Fungsi : Melihat waktu task id yang telah dikirim ke BPJS

Method : POST

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Request:

    {
        "kodebooking": "Y03-20#1617068533"
    }
    
    
    
    {
        "kodebooking": "{kodebooking yang didapat dari servis tambah antrean}",
    } 

Response:
Respon : Perlu dilakukan dekripsi disisi client


    {
        "response": {
            "list": [
                {
                "wakturs": "16-03-2021 11:32:49 WIB",
                "waktu": "24-03-2021 12:55:23 WIB",
                "taskname": "mulai waktu tunggu admisi",
                "taskid": 1,
                "kodebooking": "Y03-20#1617068533"
                }
            ]
        },
        "metadata": {
            "code": 200,
            "message": "OK"
        }
    }
    