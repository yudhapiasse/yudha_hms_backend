{BASE URL}/{Service Name}/SEP/Internal/delete
Fungsi : Hapus SEP Internal

Method : DELETE

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
           "request": {
              "t_sep": {
                 "noSep": "0301R0110421V000385",
                 "noSurat": "0301R0110421N000088",
                 "tglRujukanInternal": "2021-04-11",
                 "kdPoliTuj": "PAR",
                 "user": "Coba Ws"
              }
           }
        } 
                    
                                     
                                     
                                                
        {
           "request": {
              "t_sep": {
                 "noSep": "{nosep}",
                 "noSurat": "{nosurat}",
                 "tglRujukanInternal": "{tglRujukanInternal, format : yyyy-MM-dd",
                 "kdPoliTuj": "{kdPoli, 3 digit}",
                 "user": "{user}"
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
                                     
                      