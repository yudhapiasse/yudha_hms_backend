JSON
{BASE URL}/{Service Name}/Rujukan/insert
Fungsi : Insert Rujukan

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
       "request": {
          "t_rujukan": {
             "noSep": "0301R0011017V000014",
             "tglRujukan": "2017-11-08",
             "ppkDirujuk": "0301R002",
             "jnsPelayanan": "1",
             "catatan": "test",
             "diagRujukan": "A00.1",
             "tipeRujukan": "1",
             "poliRujukan": "INT",
             "user": "Coba Ws"
          }
       }
    }                  
                                 
                                 
                                                    
    {
       "request": {
          "t_rujukan": {
             "noSep": "{nomor sep}",
             "tglRujukan": "{tanggal rujukan format : yyyy-mm-dd}",
             "ppkDirujuk": "{faskes dirujuk -> data di referensi faskes}",
             "jnsPelayanan": "{jenis pelayanan -> 1.R.Inap 2.R.Jalan}",
             "catatan": "{catatan rujukan}",
             "diagRujukan": "{kode diagnosa rujukan -> data di referensi diagnosa}",
             "tipeRujukan": "{tipe rujukan -> 0.penuh, 1.Partial 2.rujuk balik}",
             "poliRujukan": "{kode poli rujukan -> data di referensi poli}",
             "user": "{user pemakai}"
          }
       }
    }                     

Response:
                                                
    {
       "metaData": {
          "code": "200",
          "message": "OK"
       },
       "response": {
          "rujukan": {
             "AsalRujukan": {
                "kode": "0301R001",
                "nama": "RSUP DR M JAMIL PADANG"
             },
             "diagnosa": {
                "kode": "A00.1",
                "nama": "A00.1 - Cholera due to Vibrio cholerae 01, biovar eltor"
             },
             "noRujukan": "0301R0011117B001126",
             "peserta": {
                "asuransi": "-",
                "hakKelas": null,
                "jnsPeserta": "PNS PUSAT",
                "kelamin": "Laki-Laki",
                "nama": "ZIYADUL",
                "noKartu": "0000000110156",
                "noMr": "123456",
                "tglLahir": "2008-02-05"
             },
             "poliTujuan": {
                "kode": "INT",
                "nama": "Poli Penyakit Dalam"
             },
             "tglRujukan": "2017-11-08",
             "tujuanRujukan": {
                "kode": "0301R002",
                "nama": "RS JIWA ULU GADUT"
             }
          }
       }
    }

catatan : untuk tipe rujukan 1 maka response adalah null
                                 