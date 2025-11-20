{BASE URL}/{Service Name}/SEP/2.0/insert
Fungsi : Insert SEP versi 2.0

Method : POST

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
           "request":{
              "t_sep":{
                 "noKartu":"0001105689835",
                 "tglSep":"2021-07-30",
                 "ppkPelayanan":"0301R011",
                 "jnsPelayanan":"1",
                 "klsRawat":{
                    "klsRawatHak":"2",
                    "klsRawatNaik":"1",
                    "pembiayaan":"1",
                    "penanggungJawab":"Pribadi"
                 },
                 "noMR":"MR9835",
                 "rujukan":{
                    "asalRujukan":"2",
                    "tglRujukan":"2021-07-23",
                    "noRujukan":"RJKMR9835001",
                    "ppkRujukan":"0301R011"
                 },
                 "catatan":"testinsert RI",
                 "diagAwal":"E10",
                 "poli":{
                    "tujuan":"",
                    "eksekutif":"0"
                 },
                 "cob":{
                    "cob":"0"
                 },
                 "katarak":{
                    "katarak":"0"
                 },
                 "jaminan":{
                    "lakaLantas":"0",
                    "noLP":"12345",
                    "penjamin":{
                       "tglKejadian":"",
                       "keterangan":"",
                       "suplesi":{
                          "suplesi":"0",
                          "noSepSuplesi":"",
                          "lokasiLaka":{
                             "kdPropinsi":"",
                             "kdKabupaten":"",
                             "kdKecamatan":""
                          }
                       }
                    }
                 },
                 "tujuanKunj":"0",
                 "flagProcedure":"",
                 "kdPenunjang":"",
                 "assesmentPel":"",
                 "skdp":{
                    "noSurat":"0301R0110721K000021",
                    "kodeDPJP":"31574"
                 },
                 "dpjpLayan":"",
                 "noTelp":"081111111101",
                 "user":"Coba Ws"
              }
           }
        }
                    
                                     
                                     
                                                
        {
           "request":{
              "t_sep":{
                 "noKartu":"{nokartu BPJS}",
                 "tglSep":"{tanggal penerbitan sep format yyyy-mm-dd}",
                 "ppkPelayanan":"{kode faskes pemberi pelayanan}",
                 "jnsPelayanan":"{jenis pelayanan = 1. r.inap 2. r.jalan}",
                 "klsRawat":{
                    "klsRawatHak":"{sesuai kelas rawat peserta, 1. Kelas 1, 2. Kelas 2, 3. Kelas 3}",
                    "klsRawatNaik":"{diisi jika naik kelas rawat, 1. VVIP, 2. VIP, 3. Kelas 1, 4. Kelas 2, 5. Kelas 3, 6. ICCU, 7. ICU, 8. Diatas Kelas 1}",
                    "pembiayaan":"{1. Pribadi, 2. Pemberi Kerja, 3. Asuransi Kesehatan Tambahan. diisi jika naik kelas rawat}",
                    "penanggungJawab":"{Contoh: jika pembiayaan 1 maka penanggungJawab=Pribadi. diisi jika naik kelas rawat}"
                 },
                 "noMR":"{nomor medical record RS}",
                 "rujukan":{
                    "asalRujukan":"{asal rujukan ->1.Faskes 1, 2. Faskes 2(RS)}",
                    "tglRujukan":"{tanggal rujukan format: yyyy-mm-dd}",
                    "noRujukan":"{nomor rujukan}",
                    "ppkRujukan":"{kode faskes rujukam -> baca di referensi faskes}"
                 },
                 "catatan":"{catatan peserta}",
                 "diagAwal":"{diagnosa awal ICD10 -> baca di referensi diagnosa}",
                 "poli":{
                    "tujuan":"{kode poli -> baca di referensi poli}",
                    "eksekutif":"{poli eksekutif -> 0. Tidak 1.Ya}""
                 },
                 "cob":{
                    "cob":"{cob -> 0.Tidak 1. Ya}"
                 },
                 "katarak":{
                    "katarak":"{katarak --> 0.Tidak 1.Ya}"
                 },
                 "jaminan":{
                    "lakaLantas":" 0 : Bukan Kecelakaan lalu lintas [BKLL], 1 : KLL dan bukan kecelakaan Kerja [BKK], 2 : KLL dan KK, 3 : KK",
                    "noLP":"{No. LP}",
                    "penjamin":{
                       "tglKejadian":"{tanggal kejadian KLL format: yyyy-mm-dd}",
                       "keterangan":"{Keterangan Kejadian KLL}",
                       "suplesi":{
                          "suplesi":"{Suplesi --> 0.Tidak 1. Ya}",
                          "noSepSuplesi":"{No.SEP yang Jika Terdapat Suplesi}",
                          "lokasiLaka":{
                             "kdPropinsi":"{Kode Propinsi}",
                             "kdKabupaten":"{Kode Kabupaten}",
                             "kdKecamatan":"{Kode Kecamatan}"
                          }
                       }
                    }
                 },
                 "tujuanKunj":{"0": Normal, 
                               "1": Prosedur, 
                               "2": Konsul Dokter},
                 "flagProcedure":{"0": Prosedur Tidak Berkelanjutan, 
                                  "1": Prosedur dan Terapi Berkelanjutan} ==> diisi "" jika tujuanKunj = "0",
                 "kdPenunjang":{"1": Radioterapi, 
                                "2": Kemoterapi, 
                                "3": Rehabilitasi Medik, 
                                "4": Rehabilitasi Psikososial, 
                                "5": Transfusi Darah, 
                                "6": Pelayanan Gigi, 
                                "7": Laboratorium, 
                                "8": USG, 
                                "9": Farmasi, 
                                "10": Lain-Lain, 
                                "11": MRI, 
                                "12": HEMODIALISA} ==> diisi "" jika tujuanKunj = "0",
                 "assesmentPel":{"1": Poli spesialis tidak tersedia pada hari sebelumnya, 
                                 "2": Jam Poli telah berakhir pada hari sebelumnya, 
                                 "3": Dokter Spesialis yang dimaksud tidak praktek pada hari sebelumnya, 
                                 "4": Atas Instruksi RS} ==> diisi jika tujuanKunj = "2" atau "0" (politujuan beda dengan poli rujukan dan hari beda),
                                 "5": Tujuan Kontrol,
                 "skdp":{
                    "noSurat":"{Nomor Surat Kontrol}",
                    "kodeDPJP":"{kode dokter DPJP --> baca di referensi dokter DPJP}"
                 },
                 "dpjpLayan":"000002", (tidak diisi jika jnsPelayanan = "1" (RANAP),
                 "noTelp":"{nomor telepon}",
                 "user":"{user pembuat SEP}"
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
                "assestmenPel": "1",
                "catatan": "testinsert RJ",
                "diagnosa": "A15 - Respiratory tuberculosis, bacteriologically and histologically confirmed",
                "flagProcedure": "",
                "informasi": {
                "dinsos": null,
                "eSEP": "True",
                "noSKTM": null,
                "prolanisPRB": null
            },
            "jnsPelayanan": "R.Jalan",
            "kdPenunjang": "",
            "kdPoli": "INT",
            "kelasRawat": "-",
            "noRujukan": "0050B1070223P000004",
            "noSep": "0301R0010323V000039",
            "penjamin": "-",
            "peserta": {
                "asuransi": "-",
                "hakKelas": "Kelas 3",
                "jnsPeserta": "PBI (APBN)",
                "kelamin": "Perempuan",
                "nama": "ARSTNUU",
                "noKartu": "0002802875185",
                "noMr": "MR5185",
                "tglLahir": "1944-02-24"
            },
            "poli": "PENYAKIT DALAM",
            "poliEksekutif": "Tidak",
            "tglSep": "2023-03-30",
            "tujuanKunj": "2"
            }
        }
    }
        