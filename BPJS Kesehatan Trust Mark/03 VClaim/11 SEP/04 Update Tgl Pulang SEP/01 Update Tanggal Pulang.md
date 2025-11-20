{BASE URL}/{Service Name}/Sep/updtglplg
Fungsi : Update tanggal pulang SEP

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

            {  
                "request": 
                    {    
                    "t_sep":
                        {
                            noSep":"0301R00105160000569",
                            "tglPlg":"2016-06-12 09:00:00",
                            "ppkPelayanan":"0301R001"
                        }
                    }
            }                  
                                     
                                     
                                                
            {  
                "request": 
                    {    
                    "t_sep":
                        {
                            noSep":"{Nomor SEP}",
                            "tglPlg":"{Tanggal Pulang formt yyyy-MM-dd hh:mm:ss}",
                            "ppkPelayanan":"{PPK Pelayanan SEP}"
                        }
                    }
            }              

Response:

                    