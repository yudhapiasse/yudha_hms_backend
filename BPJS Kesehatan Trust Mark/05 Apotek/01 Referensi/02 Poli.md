{Base URL}/{Service Name}/referensi/poli/{Parameter}
Fungsi : Daftar Obat Poli

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter : Kode atau Nama Poli


    {
        "response": 
            {
                "poli": 
                 [
                    {
                        "kode": "ICU",
                        "nama": "Intensive Care Unit"
                    },
                    {
                        "kode": "INT",
                        "nama": "Poli Penyakit Dalam"
                    },
                    {
                        "kode": "IVP",
                        "nama": "Intravena Pydografi"
                    }
                ]
            },
        "metaData": 
            {
            "code": "200",
            "message": "OK"
            }
    }
	
        