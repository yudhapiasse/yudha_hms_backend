{BASE URL}/{Service Name}/Rujukan/2.0/Update
Fungsi : Update Rujukan 2.0

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
         "request": {
                "t_rujukan": {
                            "noRujukan": "0301R0010321V000003",
                            "tglRujukan": "2021-03-18",
                            "tglRencanaKunjungan":"2021-03-19",
                            "ppkDirujuk": "03010402",
                            "jnsPelayanan": "1",
                            "catatan": "test",
                            "diagRujukan": "A15",
                            "tipeRujukan": "2", (0 Penuh, 1 Partial, 2 balik PRB)
                            "poliRujukan": "", (kosong untuk tipe rujukan 2)
                            "user": "Coba Ws"
                }
         }
    }            
                                 
                                 
                                                    
    {
         "request": {
                "t_rujukan": {
                            "noRujukan": "{nomor rujukan}",
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
       "response": "0301R0011117B000014" 
    }

         