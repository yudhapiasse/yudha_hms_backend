URL : RS mengirimkan url masing-masing ws yang sudah dibuat untuk diakses oleh sistem BPJS

Fungsi : Membuat token

Method : GET

Format : Json

Header :
x-username: {user akses}
x-password: {password akses}


    {
        "response": {
            "token": "1231242353534645645"
        },
            "metadata": {
            "message": "Ok",
            "code": 200
        }
    }


Catatan:

User dan password yang diberikan ke BPJS Kesehatan untuk mengakses WS yang dibuat oleh RS.