
{BASE URL}/{Service Name}/api/rs/validate
Fungsi : API Data Riwayat Pelayanan

Method : POST

Format : Json

Content-Type: application/json

Request:

    {
        "param": "2200009338321",
        "kodedokter": 11111
    }
    
    
    
    {
        "param": "{nomorkartu}",
        "kodedokter": {kodedokter}
    }   

Response

    {
        "response": {
        "url": "https://dvlp.bpjs-kesehatan.go.id/ihs/history?token=e6b610b4-2960-46a3-8420-de879756dce3"
    },
    "metaData": {
        "code": 200,
        "message": "Sukses"
    }
    }
   