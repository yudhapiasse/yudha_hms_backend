{BASE URL}/{Service Name}/SEP/1.1/insert
Fungsi : Insert SEP versi 1.1

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

            {
               "request": {
                  "t_sep": {
                     "noKartu": "0001112230666",
                     "tglSep": "2017-10-18",
                     "ppkPelayanan": "0301R001",
                     "jnsPelayanan": "2",
                     "klsRawat": "3",
                     "noMR": "123456",
                     "rujukan": {
                        "asalRujukan": "1",
                        "tglRujukan": "2017-10-17",
                        "noRujukan": "1234567",
                        "ppkRujukan": "00010001"
                     },
                     "catatan": "test",
                     "diagAwal": "A00.1",
                     "poli": {
                        "tujuan": "INT",
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
                            "penjamin": "1",
                            "tglKejadian": "2018-08-06",
                            "keterangan": "kll",
                            "suplesi": {
                                "suplesi": "0",
                                "noSepSuplesi": "0301R0010718V000001",
                                "lokasiLaka": {
                                    "kdPropinsi": "03",
                                    "kdKabupaten": "0050",
                                    "kdKecamatan": "0574"
                                    }
                            }
                        }
                     },
                     "skdp": {
                        "noSurat": "000002",
                        "kodeDPJP": "31661"
                     },
                     "noTelp": "081919999",
                     "user": "Coba Ws"
                  }
               }
            }                    
                                     
                                     
                                                        
            {
               "request": {
                  "t_sep": {
                     "noKartu": "{nokartu BPJS}",
                     "tglSep": "{tanggal penerbitan sep format yyyy-mm-dd}",
                     "ppkPelayanan": "{kode faskes pemberi pelayanan}",
                     "jnsPelayanan": "{jenis pelayanan = 1. r.inap 2. r.jalan}",
                     "klsRawat": "{kelas rawat 1. kelas 1, 2. kelas 2 3.kelas 3}",
                     "noMR": "{nomor medical record RS}",
                     "rujukan": {
                        "asalRujukan": "{asal rujukan ->1.Faskes 1, 2. Faskes 2(RS)}",
                        "tglRujukan": "{tanggal rujukan format: yyyy-mm-dd}",
                        "noRujukan": "{nomor rujukan}",
                        "ppkRujukan": "{kode faskes rujukam -> baca di referensi faskes}"
                     },
                     "catatan": "{catatan peserta}",
                     "diagAwal": "{diagnosa awal ICD10 -> baca di referensi diagnosa}",
                     "poli": {
                        "tujuan": "{kode poli -> baca di referensi poli}",
                        "eksekutif": "{poli eksekutif -> 0. Tidak 1.Ya}"
                     },
                     "cob": {
                        "cob": "{cob -> 0.Tidak 1. Ya}"
                     },
                     "katarak": {
                        "katarak": "{katarak --> 0.Tidak 1.Ya}"
                     },
                     "jaminan": {
                        "lakaLantas": "Kecelakaan Lalu Lintas --> 0.Tidak 1.Ya",
                        "penjamin": {
                            "penjamin": "{penjamin lakalantas -> 1=Jasa raharja PT, 2=BPJS Ketenagakerjaan, 3=TASPEN PT, 4=ASABRI PT} jika lebih dari 1 isi -> 1,2 (pakai delimiter koma)}",
                            "tglKejadian": "{tanggal kejadian KLL format: yyyy-mm-dd}",
                            "keterangan": "{Keterangan Kejadian KLL}",
                            "suplesi": {
                                "suplesi": "{Suplesi --> 0.Tidak 1. Ya}",
                                "noSepSuplesi": "{No.SEP yang Jika Terdapat Suplesi}",
                                "lokasiLaka": {
                                    "kdPropinsi": "{Kode Propinsi}",
                                    "kdKabupaten": "{Kode Kabupaten}",
                                    "kdKecamatan": "{Kode Kecamatan}"
                                    }
                            }
                        }
                     },
                     "skdp": {
                        "noSurat": "{Nomor Surat Kontrol}",
                        "kodeDPJP": "{kode dokter DPJP --> baca di referensi dokter DPJP}"
                     },
                     "noTelp": "{nomor telepon}",
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
               "response": {
                  "sep": {
                     "catatan": "test",
                     "diagnosa": "A00.1 - Cholera due to Vibrio cholerae 01, biovar eltor",
                     "jnsPelayanan": "R.Inap",
                     "kelasRawat": "1",
                     "noSep": "0301R0011117V000008",
                     "penjamin": "-",
                     "peserta": {
                        "asuransi": "-",
                        "hakKelas": "Kelas 1",
                        "jnsPeserta": "PNS PUSAT",
                        "kelamin": "Laki-Laki",
                        "nama": "ZIYADUL",
                        "noKartu": "0001112230666",
                        "noMr": "123456",
                        "tglLahir": "2008-02-05"
                     },
                     "informasi:": {
                        "Dinsos":null,
                        "prolanisPRB":null,
                        "noSKTM":null
                     },
                     "poli": "-",
                     "poliEksekutif": "-",
                     "tglSep": "2017-10-12"
                  }
               }
            }
                     