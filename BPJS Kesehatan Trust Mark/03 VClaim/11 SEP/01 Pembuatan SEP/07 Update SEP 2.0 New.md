{BASE URL}/{Service Name}/SEP/2.0/update
Fungsi : Update SEP versi 2.0

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

    {
     "request": {
        "t_sep": {
                "noSep": "0301R0110521V000037",
                "klsRawat":{
                                "klsRawatHak":"3",
                                "klsRawatNaik":"",
                                "pembiayaan":"",
                                "penanggungJawab":""
                              },
                "noMR": "00469120",
                "catatan": "",
                "diagAwal": "E10",
                "poli": {
                        "tujuan": "IGD",
                        "eksekutif": "0"
                },
                "cob": {
                        "cob": "0"
                },
                "katarak": {
                        "katarak": "0"
                },
                "jaminan": {
                        "lakaLantas": "0",
                        "penjamin": {
                                "tglKejadian": "",
                                "keterangan": "",
                                "suplesi": {
                                        "suplesi": "0",
                                        "noSepSuplesi": "",
                                        "lokasiLaka": {
                                                "kdPropinsi": "",
                                                "kdKabupaten": "",
                                                "kdKecamatan": ""
                                        }
                                }
                        }
                },
                "dpjpLayan":"46",
                "noTelp": "08522038363",
                "user": "Cobaws"
        }
      }
    }        
                                 
                                 
                    
                                     
                                     
                                                
                                                    
    {
     "request": {
        "t_sep": {
                "noSep": "{nomor sep}",
                "klsRawat":{
                                "klsRawatHak":"3",
                                "klsRawatNaik":"",
                                "pembiayaan":"",
                                "penanggungJawab":""
                              },
                "noMR": "{nomor medical record RS}",
                "catatan": "{catatan peserta}",
                "diagAwal": "{diagnosa awal ICD10 -> baca di referensi diagnosa}",
                "poli": {
                        "tujuan": "IGD",
                        "eksekutif": "{poli eksekutif -> 0. Tidak 1.Ya}"
                },
                "cob": {
                        "cob": "{cob -> 0.Tidak 1. Ya}"
                },
                "katarak": {
                        "katarak": "{katarak --> 0.Tidak 1.Ya}"
                },
                "jaminan": {
                        "lakaLantas":" 0 : Bukan Kecelakaan lalu lintas [BKLL], 1 : KLL dan bukan kecelakaan Kerja [BKK], 2 : KLL dan KK, 3 : KK",
                        "penjamin": {
                                "tglKejadian": "{tgl kejadian KLL (yyyy-mm-dd)}",
                                "keterangan": "{keterangan kejadian}",
                                "suplesi": {
                                        "suplesi": "0",
                                        "noSepSuplesi": "{no SEP suplesi --> diambil dari Potensi Suplesi Jasa Raharja}",
                                        "lokasiLaka": {
                                                "kdPropinsi": "{kode propinsi}",
                                                "kdKabupaten": "{kode kabupaten}",
                                                "kdKecamatan": "{kode kecamatan}"
                                        }
                                }
                        }
                },
                "dpjpLayan":"46",
                "noTelp": "{nomor telepon peserta/pasien}",
                "user": "{user pembuat SEP}"
        }
      }
    }        

Response:


        {
          "metaData": {
            "code": "200",
            "message": "Sukses"
          },
          "response": "1101R0070420V000017"
        }
                                     
                                     
           