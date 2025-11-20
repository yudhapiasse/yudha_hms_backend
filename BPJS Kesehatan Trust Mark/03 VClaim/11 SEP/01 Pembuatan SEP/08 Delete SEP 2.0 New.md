{BASE URL}/{Service Name}/SEP/2.0/delete
Fungsi : Hapus SEP versi 2.0

Method : DELETE

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:



    {
       "request": {
          "t_sep": {
             "noSep": "0301R0011017V000007",
             "user": "Coba Ws"
          }
       }
    }
                
                                     
                                     
                                                
                                                    
    {
       "request": {
          "t_sep": {
             "noSep": "{nomor SEP}",
             "user": "{user pengguna SEP}"
          }
       }
    }       


Response:


        {
            metaData: 
                {
                code: "200"
                message: "OK"
                }
            response: "0301R0011017V000007"
        }
                