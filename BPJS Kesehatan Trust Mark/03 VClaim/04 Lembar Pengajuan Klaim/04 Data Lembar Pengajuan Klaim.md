{BASE URL}/{Service Name}/LPK/TglMasuk/{Parameter 1}/JnsPelayanan/{Paramater 2}
Fungsi : Pencarian data peserta berdasarkan NIK Kependudukan

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : Tanggal Masuk - format : yyyy-MM-dd

Parameter 2 : Jenis Pelayanan 1. Inap 2.Jalan


        {
           "metaData": {
              "code": "200",
              "message": "OK"
           },
           "response": {
              "lpk": {
                 "list": [
                    {
                       "DPJP": {
                          "dokter": {
                             "kode": "3",
                             "nama": "Satro Jadhit, dr"
                          }
                       },
                       "diagnosa": {
                          "list": [
                             {
                                "level": "1",
                                "list": {
                                   "kode": "N88.1",
                                   "nama": "Old laceration of cervix uteri"
                                }
                             },
                             {
                                "level": "2",
                                "list": {
                                   "kode": "A00.1",
                                   "nama": "Cholera due to Vibrio cholerae 01, biovar eltor"
                                }
                             }
                          ]
                       },
                       "jnsPelayanan": "1",
                       "noSep": "0301R0011017V000014",
                       "perawatan": {
                          "caraKeluar": {
                             "kode": "1",
                             "nama": "Atas Persetujuan Dokter"
                          },
                          "kelasRawat": {
                             "kode": "1",
                             "nama": "VVIP"
                          },
                          "kondisiPulang": {
                             "kode": "1",
                             "nama": "Sembuh"
                          },
                          "ruangRawat": {
                             "kode": "3",
                             "nama": "Ruang Melati I"
                          },
                          "spesialistik": {
                             "kode": "1",
                             "nama": "Spesialis Penyakit dalam"
                          }
                       },
                       "peserta": {
                          "kelamin": "L",
                          "nama": "123456",
                          "noKartu": "0000000001231",
                          "noMR": "123456",
                          "tglLahir": "2008-02-05"
                       },
                       "poli": {
                          "eksekutif": "0",
                          "poli": {
                             "kode": "INT"
                          }
                       },
                       "procedure": {
                          "list": [
                             {
                                "list": {
                                   "kode": "00.82",
                                   "nama": "Revision of knee replacement, femoral component"
                                }
                             },
                             {
                                "list": {
                                   "kode": "00.83",
                                   "nama": "Revision of knee replacement,patellar component"
                                }
                             }
                          ]
                       },
                       "rencanaTL": null,
                       "tglKeluar": "2017-10-30",
                       "tglMasuk": "2017-10-30"
                    }
                 ]
              }
           }
        }             
                                     
            