{BASE URL}/{Service Name}/Sep/pengajuanSEP
Fungsi : Pengajuan SEP

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

         {
           "request": {
              "t_sep": {
                 "noKartu": "0001300759569",
                 "tglSep": "2021-03-26",
                 "jnsPelayanan": "1",
                 "jnsPengajuan": "2",
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
                 "jnsPengajuan": "{}jenis pengajuan (1. pengajuan backdate, 2. pengajuan finger print)}"
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
                                     
                             