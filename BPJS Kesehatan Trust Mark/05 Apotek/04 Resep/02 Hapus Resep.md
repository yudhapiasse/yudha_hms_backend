{Base URL}/{Service Name}/hapusresep
Fungsi : Hapus Resep

Method : DELETE

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
            "nosjp": "1202A00201210000032",
            "refasalsjp": "1202R0010121V000325",
            "noresep": "0SI44"
        }                
                                     
                                     
                                                        
        {
            "nosjp": "1202A00201210000032",
            "refasalsjp": "1202R0010121V000325",
            "noresep": "0SI44"
        }                

Response:

        {
        "metaData": {
            "code": "200",
            "message": "OK"
        },
        "response": null
        }