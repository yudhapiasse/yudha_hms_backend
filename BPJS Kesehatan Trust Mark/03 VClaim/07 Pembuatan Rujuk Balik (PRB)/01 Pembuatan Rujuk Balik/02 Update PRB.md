{BASE URL}/{Service Name}/PRB/Update
Fungsi : Update PRB

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
           "request":{
              "t_prb":{
                 "noSrb":"9118924",
                 "noSep":"0301R0011117V000008",
                 "alamat":"jl. Merdekah",
                 "email":"emailkudisadap@gmail.com",
                 "kodeDPJP":"271190",
                 "keterangan":"Capek Kerja",
                 "saran":"Pasien harus Cuti,Kecapekan Kerja dan Kerja.",
                 "user":"123456",
                 "obat":[
                    {
                       "kdObat":"00196999124",
                       "signa1":"3",
                       "signa2":"1",
                       "jmlObat":"5"
                    },
                    {
                       "kdObat":"00011999918",
                       "signa1":"3",
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
              "noSrb":"{Nomor Surat Rujuk Balik(SRB)}",
              "noSep":"{nomor sep}",
              "alamat":"{alamat lengkap pasien}",
              "email":"{alamat email pasien}",
              "kodeDPJP":"{kode dokter DPJP --> lihat referensi}",
              "keterangan":"{keterangan pasien}",
              "saran":"{saran dari dokter DPJP}",
              "user":"{user entri harus diisi numerik}",
              "obat":
                [
                    { 
                        "kdObat":"{kode obat generik --> lihat referensi}",
                        "signa1":"{signa 1}",
                        "signa2":"{signa 2}",
                        "jmlObat":"{jumlah obat}"
                    },
                    { 
                        "kdObat":"{kode obat generik --> lihat referensi}",
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
            "metaData": 
            {
                "code": "200",
                "message": "OK"
            },
            "response": "9111924"
        }
                                     