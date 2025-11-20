{BASE URL}/{Service Name}/Rujukan/Khusus/delete
Fungsi : Delete Rujukan Khusus

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
       "request": {
                "t_rujukan": {
                          "idRujukan": "98865",
                          "noRujukan": "0301U0331019P003283",
                          "user": "Coba Ws"
                  }
        }
    }      
                                 
                                 
                                                    
        "request": {
                "t_rujukan": {
                          "idRujukan": "{id rujukan}",
                          "noRujukan": "{nomor rujukan}",
                          "user": "{user ws}"
                  }
        }
    }

Response:

    {
      "metaData": {
        "code": "200",
        "message": "OK"
      },
      "response": "98865"
    }
                                 
        