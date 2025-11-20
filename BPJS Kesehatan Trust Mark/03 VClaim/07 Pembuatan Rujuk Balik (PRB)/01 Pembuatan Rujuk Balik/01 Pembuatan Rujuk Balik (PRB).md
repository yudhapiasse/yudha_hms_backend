JSON
{BASE URL}/{Service Name}/PRB/insert
Fungsi : Insert Rujuk balik

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

            {
              "request":
               {
              "t_prb":
                {  
                  "noSep":"1101R0070118V999006",
                  "noKartu":"0009999979951",
                  "alamat":"Jln. Medan Merdekah",
                  "email":"email@gmail.com",
                  "programPRB":"09",
                  "kodeDPJP":"27590",
                  "keterangan":"Kecapekan kerja",
                  "saran":"Pasien harus olahraga bersama setiap minggu dan cuti, edukasi agar jangan disuruh kerja terus, lama lama stress.",
                  "user":"1234567",
                  "obat":
                    [
                        { 
                            "kdObat":"00196120124",
                            "signa1":"1",
                            "signa2":"1",
                            "jmlObat":"11"
                        },
                        { 
                            "kdObat":"00011220018",
                            "signa1":"1",
                            "signa2":"1",
                            "jmlObat":"10"
                        }
                    ]      
                }
               }
            }                
                                     
                                     
                                                        
        {
        "request":
        {
        "t_prb":
            {  
                "noSep":"{no sep rawat jalan}",
                "noKartu":"nomor kartu peserta",
                "alamat":"alamat lengkap peserta",
                "email":"alamat email",
                "programPRB":"{}kode program PRB --> lihat referensi}",
                "kodeDPJP":"{kode DPJP  --> lihat referensi}",
                "keterangan":"{keterangan }",
                "saran":"{saran dokter pemberi rujuk balik}",
                "user":"{user harus diisi numeric}",
                "obat":
                [
                    { 
                        "kdObat":"{kode obat generik --> lihat referensi obat generik}",
                        "signa1":"{signa 1}",
                        "signa2":"{signa 2}",
                        "jmlObat":"{jumlah obat}"
                    },
                    { 
                        "kdObat":"{kode obat generik --> lihat referensi obat generik}",
                        "signa1":"{signa 1}",
                        "signa2":"{signa 2}",
                        "jmlObat":"{jumlah obat}"
                    }
                ]      
                }
            }
        }                


Response:

        {
          "metaData": {
            "code": "200",
            "message": "Sukses"
          },
          "response": {
            "DPJP": {
              "kode": "27510",
              "nama": "Suwito, dr. Sp.PD"
            },
            "keterangan": "Capek Kerja",
            "noSRB": "94111924",
            "obat": {
              "list": [
                {
                  "jmlObat": "1",
                  "nmObat": "Vitamin B1 (Thiamin HCl) tab 50 mg",
                  "signa": "1 x 1"
                },
                {
                  "jmlObat": "1",
                  "nmObat": "Analog Insulin Mix Acting Inj 100 UI/ml ",
                  "signa": "1 x 1"
                
              ]
            },
            "peserta": {
              "alamat": "Jl. Medan Merdekah",
              "asalFaskes": {
                "kode": "01691101",
                "nama": "Klinik KALIGANGSAS"
              },
              "email": "email@gmail.com",
              "kelamin": "P",
              "nama": "SITI JUBAEDAH",
              "noKartu": "000999979951",
              "noTelepon": "081234567890",
              "tglLahir": "1945-09-06"
            },
            "programPRB": "Systemic Lupus Erythematosus",
            "saran": "Pasien harus cuti setiap minggu, edukasi agar jangan disuruh kerja terus, lama lama stress..",
            "tglSRB": "2018-01-08"
          }
        }
                                     
                     