{BASE URL}/{Service Name}/Rujukan/Khusus/List/Bulan/{parameter 1}/Tahun/{parameter 2}
Fungsi : Data Khusus

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter 1: Bulan (1,2,3,4,5,6,7,8,9,10,11,12)

Parameter 2: Tahun (4 digit)



    {
      "metaData": {
        "code": "200",
        "message": "OK"
      },
      "response": {
        "rujukan": [
          {
            "idrujukan": "98866",
            "norujukan": "0301U0331019P003283",
            "nokapst": "0000016553957",
            "nmpst": "MUZNI MUKHTAR",
            "diagppk": "N18",
            "tglrujukan_awal": "2021-03-22",
            "tglrujukan_berakhir": "2021-06-19"
          }
        ]
      }
    }