{BASE URL}/{Service Name}/Rujukan/2.0/insert
Fungsi : Insert Rujukan 2.0

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
         "request": {
                        "t_rujukan": {
                                 "noSep": "0301R0010321V000003",
                                 "tglRujukan": "2021-03-18",
                                 "tglRencanaKunjungan":"2021-03-19",
                                 "ppkDirujuk": "03010402",
                                 "jnsPelayanan": "1",
                                 "catatan": "test",
                                 "diagRujukan": "A15",
                                 "tipeRujukan": "2",
                                 "poliRujukan": "",
                                 "user": "Coba Ws"
                        }
         }
    }             
                                 
                                 
                                                    
    {
         "request": {
                "t_rujukan": {
                            "noSep": "{nomor sep}",
                            "tglRujukan": "{tanggal rujukan, format : yyyy-MM-dd}",
                            "tglRencanaKunjungan":"{tanggal rencana kunjungan, format : yyyy-MM-dd}",
                            "ppkDirujuk": "{kode faskes, 8 digit}",
                            "jnsPelayanan": "{1-> rawat inap, 2-> rawat jalan}",
                            "catatan": "{catatan}",
                            "diagRujukan": "{kode diagnosa}",
                            "tipeRujukan": "{0->Penuh, 1->Partial, 2->balik PRB}",
                            "poliRujukan": "{kosong untuk tipe rujukan 2, harus diisi jika 0 atau 1}",
                            "user": "{user ws}"
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
            "kode": "0301R001d",
            "nama": "RSUP DR M JAMIL PADANG"
          },
          "diagnosa": {
            "kode": "A15",
            "nama": "A15 - Respiratory tuberculosis, bacteriologically and histologically confirmed"
          },
          "noRujukan": "0301R0010321B000012",
          "peserta": {
            "asuransi": "-",
            "hakKelas": null,
            "jnsPeserta": "PBI (APBD)",
            "kelamin": "Laki-Laki",
            "nama": "FADLAN LISMI AZIZ",
            "noKartu": "0001329783085",
            "noMr": "00754610",
            "tglLahir": "2006-02-20"
          },
          "poliTujuan": {
            "kode": "",
            "nama": ""
          },
          "tglBerlakuKunjungan": "2021-06-16",
          "tglRencanaKunjungan": "2021-03-19",
          "tglRujukan": "2021-03-18",
          "tujuanRujukan": {
            "kode": "03010402",
            "nama": "PEGAMBIRAN"
          }
        }
      }
    }
           