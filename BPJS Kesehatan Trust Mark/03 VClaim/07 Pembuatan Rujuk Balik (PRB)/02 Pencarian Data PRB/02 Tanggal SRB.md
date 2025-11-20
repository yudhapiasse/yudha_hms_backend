{Base URL}/{Service Name}/prb/tglMulai{Parameter 1}/tglAkhir{Parameter 2}
Fungsi : Pencarian data PRB (Rujuk Balik) Berdasarkan Tanggal SRB

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Tgl. Mulai (yyyy-mm-dd)

Parameter 2 : Tgl. Mulai (yyyy-mm-dd)


        {
          "metaData": {
            "code": "200",
            "message": "OK"
          },
          "response": {
            "prb": {
              "list": [
                  {
                  "DPJP": {
                    "kode": "2759111",
                    "nama": "Marwoto,dr. Sp.PD"
                  },              
                  "noSEP": "1101R0070118V110086",
                  "noSRB": "9419118",
                  "peserta": {
                    "alamat": "Jl. Merdekah",
                    "email": "emailkudisadap@gmail.com",
                    "nama": "SITI RONALD",
                    "noKartu": "0011079979951",
                    "noTelepon": "081728191919191"
                  },
                  "programPRB": {
                    "kode": "01 ",
                    "nama": "Diabetes Mellitus"
                  },
                  "keterangan": "Alergi Cuti",
                  "saran": "Pasien Wajib Berlibur, Jangan Disuruh Kerja",
                  "tglSRB": "2018-01-08"
                },
                {
                  "DPJP": {
                    "kode": "2759111",
                    "nama": "Marwoto,dr. Sp.PD"
                  },
                  
                  "noSEP": "1101R0070118V110086",
                  "noSRB": "9419118",
                  "peserta": {
                    "alamat": "Jl. Merdekah",
                    "email": "emailkudisadap@gmail.com",
                    "nama": "SITI RONALD",
                    "noKartu": "0011079979951",
                    "noTelepon": "081728191919191"
                  },
                  "programPRB": {
                    "kode": "01 ",
                    "nama": "Diabetes Mellitus"
                  },
                  "keterangan": "Alergi Cuti",
                  "saran": "Pasien Wajib Berlibur, Jangan Disuruh Kerja",
                  "tglSRB": "2018-01-08"
                }
              ]
            }
          }
        }
              