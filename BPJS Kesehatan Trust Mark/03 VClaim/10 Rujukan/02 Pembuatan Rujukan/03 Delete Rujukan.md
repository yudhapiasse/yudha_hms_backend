{BASE URL}/{Service Name}/Rujukan/delete
Fungsi : Update Rujukan

Method : DELETE

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
        "request": {
            "t_rujukan": {
                "noRujukan": "0301R0011117B000015",
                "user": "Coba Ws"
            }
        }
    }   
                                 
                                 
                                                    
    {
       "request": {
          "t_rujukan": {
             "noRujukan": "{nomor rujukan}",
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
                         