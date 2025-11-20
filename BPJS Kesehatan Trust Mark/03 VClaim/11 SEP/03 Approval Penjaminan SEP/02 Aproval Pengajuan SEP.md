{BASE URL}/{Service Name}/Sep/aprovalSEP
Fungsi : Pengajuan SEP

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        Tanpa parameter jnsPengajuan maka default jnsPengajuan="1" (Approval SEP Backdate) 

        {
           "request": {
              "t_sep": {
                 "noKartu": "0003814312013",
                 "tglSep": "2017-10-26",
                 "jnsPelayanan": "1",
                 "keterangan": "Hari libur",
                 "user": "Coba Ws"
              }
           }
        }		
		

        Jika parameter jnsPengajuan ada nilai, maka approval sesuai jnsPengajuan 
 
        {
           "request": {
              "t_sep": {
                 "noKartu": "0003814312013",
                 "tglSep": "2017-10-26",
                 "jnsPelayanan": "1",
                 "jnsPengajuan": "1",
                 "keterangan": "Hari libur",
                 "user": "Coba Ws"
              }
           }
        }         
                                     
                                     
                                                
        {
           "request": {
              "t_sep": {
                 "noKartu": "{nomor kartu BPJS}",
                 "tglSep": "{tanggal penerbitan sep format yyyy-mm-dd}",
                 "jnsPelayanan": "{}jenis pelayanan (1.R.Inap 2.R.Jalan)}",
                 "jnsPengajuan": "{}jenis pengajuan (1. pengajuan backdate, 2. pengajuan finger print)}",
                 "keterangan": "{keterangan}",
                 "user": "{user pemakai}"
              }
         }

Response:

            {
                metadata: 
                    {
                    code: "200"
                    message: "OK"
                    }
                response: "0003814312013"
            }
                 