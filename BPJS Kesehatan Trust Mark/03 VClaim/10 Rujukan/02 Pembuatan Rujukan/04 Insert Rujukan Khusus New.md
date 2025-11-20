{BASE URL}/{Service Name}/Rujukan/Khusus/insert
Fungsi : Insert Rujukan Khusus

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
         "noRujukan": "0301U0331019P003283",
         "diagnosa": [
                 {"kode": "P;N18"},
                 {"kode": "S;N18.1"}
         ],
        "procedure":  [
                 {"kode": "39.95"}
         ],
         "user": "Coba Ws"
    }                
                                 
                                 
                                                    
    {
         "noRujukan": "{norujukan}",
         "diagnosa": [
                 {"kode": "{primer/sekunder};{kodediagnosa}"}
         ],
        "procedure":  [
                 {"kode": "{kodeprocedure}"}
         ],
         "user": "{user ws}"
    }              

Response:

    {
      "metaData": {
        "code": "200",
        "message": "Sukses"
      },
      "response": {
        "rujukan": {
          "norujukan": "0301U0331019P003283",
          "nokapst": "0000016553957",
          "nmpst": "MUZNI MUKHTAR",
          "diagppk": "Z49.1",
          "tglrujukan_awal": "2021-06-20",
          "tglrujukan_berakhir": "2021-09-17"
        }
      }
    }
                                 
       