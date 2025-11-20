{Base URL}/{Service Name}/prb/{Parameter 1}/nosep/{Parameter 2}

Fungsi : Pencarian data PRB (Rujuk Balik) Berdasarkan Nomor SRB

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : No. SRB Peserta

Parameter 2 : No. SEP


        {
          "metaData": {
            "code": "200",
            "message": "Sukses"
          },
          "response": {
            "prb": {
              "DPJP": {
                "kode": "275190",
                "nama": "Marwoto, dr. Sp.PD"
              },
              "noSEP": "1101R0070118V999996",
              "noSRB": "9419118",
              "obat": {
                "obat": [
                  {
                    "jmlObat": "5",
                    "kdObat": "00019990017",
                    "nmObat": "Analog Insulin Long Acting inj 100 UI/ml",
                    "signa1": "3",
                    "signa2": "1"
                  },
                  {
                    "jmlObat": "10",
                    "kdObat": "00078990062",
                    "nmObat": "Human Insulin Long Acting penfill 3 ml",
                    "signa1": "3",
                    "signa2": "1"
                  }
                ]
              },
              "peserta": {
                "alamat": "Jl. Merdekah",
                "asalFaskes": {
                  "kode": "016999901",
                  "nama": "Klinik KALI ADEM"
                },
                "email": "emailkudisadap@gmail.com",
                "kelamin": "P",
                "nama": "SITI RONALDO",
                "noKartu": "0054679979951",
                "noTelepon": "089101999101",
                "tglLahir": "1949-09-06"
              },
              "programPRB": {
                "kode": "01 ",
                "nama": "Diabetes Mellitus"
              },
              "keterangan": "Kecapekan Kerja",
              "saran": "Pasien Harus Cuti, Kebanyakan Kerja",
              "tglSRB": "2018-01-08"
            }
          }
        }