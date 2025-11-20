{Base URL}/{Service Name}/referensi/obatprb/{Parameter 1}
Fungsi : Pencarian data obat generik PRB

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1: nama obat generik


    {
      "metaData": {
        "code": "200",
        "message": "Sukses"
      },
      "response": {
        "list": [
          {
            "kode": "00019100017",
            "nama": "Analog Insulin Long Acting inj 100 UI/ml"
          },
          {
            "kode": "00012300016",
            "nama": "Analog Insulin Mix Acting inj 100 UI/ml"
          }
        ]
      }
    }
           