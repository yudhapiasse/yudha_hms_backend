{BASE URL}/{Service Name}/SEP/1.1/Update
Fungsi : Update SEP versi 1.1

Method : PUT

Format : Json

Content-Type: Application/x-www-form-urlencoded

Request:

        {
           "request": {
              "t_sep": {
                 "noSep": "0301R0011117V000008",
                 "klsRawat": "1",
                 "noMR": "123456",
                 "rujukan": {
                    "asalRujukan": "1",
                    "tglRujukan": "2017-10-23",
                    "noRujukan": "1234567",
                    "ppkRujukan": "00010001"
                 },
                 "catatan": "test",
                 "diagAwal": "B00.1",
                 "poli": {
                    "eksekutif": "0"
                 },
                 "cob": {
                    "cob": "0"
                 },
                 "katarak":{
                    "katarak":"1"
                 },
                 "skdp":{
                    "noSurat":"12313123",
                    "kodeDPJP":"39508"            
                 },
                 "jaminan": {
                    "lakaLantas":"1",
                    "penjamin":
                    {
                        "penjamin":"1",
                        "tglKejadian":"2018-08-06",				
                        "keterangan":"kll",
                        "suplesi":
                            {
                                "suplesi":"0",
                                "noSepSuplesi":"0301R0010718V000001",
                                "lokasiLaka": 
                                    {
                                    "kdPropinsi":"03",
                                    "kdKabupaten":"0050",
                                    "kdKecamatan":"0574"
                                    }
                            }					
                    }
                 },             
                 "noTelp": "081919999",
                 "user": "Coba Ws"
              }
           }
        }                
                                     
                                     
                                                
        {
           "request": {
              "t_sep": {
                 "noSep": "{nomor sep}",
                 "klsRawat": "kelas rawat 1. kelas 1, 2. kelas 2 3.kelas 3",
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
                    "eksekutif": "{poli eksekutif -> 0. Tidak 1.Ya}"
                 },
                 "cob": {
                    "cob": "{cob -> 0.Tidak 1. Ya}"
                 },
                 "katarak":{
                    "katarak":"{katarak --> 0.Tidak 1.Ya}"
                 },
                 "skdp":{
                    "noSurat":"{no surat kontrol}",
                    "kodeDPJP":"{kode dokter DPJP ->> diambil dari referensi dokter dpjp}"            
                 },
                 "jaminan": {
                    "lakaLantas":"{kejadian lakalantas -> 0. Tidak 1. Ya}",
                    "penjamin":
                    {
                        "penjamin":"{penjamin lakalantas -> 1. Jasa Raharja, 2. BPJS Ketenagakerjaan 3. TASPEN 4.ASABRI}",
                        "tglKejadian":"{tgl kejadian KLL (yyyy-mm-dd)}",				
                        "keterangan":"{keterangan kejadian}",
                        "suplesi":
                            {
                                "suplesi":"{suplesi --> 0.Tidak 1.Ya}",
                                "noSepSuplesi":"{no SEP suplesi --> diambil dari Potensi Suplesi Jasa Raharja}",
                                "lokasiLaka": 
                                    {
                                    "kdPropinsi":"{kode propinsi}",
                                    "kdKabupaten":"{kode kabupaten}",
                                    "kdKecamatan":"{kode kecamatan}"
                                    }
                            }					
                    }
                 },
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
               "response": "0301R0011117V000008"
            }
                  