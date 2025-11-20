{BASE URL}/{Service Name}/antrean/farmasi/add

Fungsi : Menambah Antrean Farmasi RS

Method : POST

Format : Json

Request:

    {
        "kodebooking": "16032021A001",
        "jenisresep": "racikan" ---> (racikan / non racikan),
        "nomorantrean": 1,
        "keterangan": ""
    }

Response:

    {
        "metadata": {
            "message": "Ok",
            "code": 200
        }
    }
    