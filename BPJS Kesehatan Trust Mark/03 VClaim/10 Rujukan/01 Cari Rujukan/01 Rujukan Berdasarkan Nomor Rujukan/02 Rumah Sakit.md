{BASE URL}/{Service Name}/Rujukan/RS/{parameter}

Fungsi : Pencarian data rujukan dari rumah sakit berdasarkan nomor rujukan

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter : Nomor Rujukan


    {
       "metaData": {
          "code": "200",
          "message": "OK"
       },
       "response": {
          "rujukan": {
             "diagnosa": {
                "kode": "I21.9",
                "nama": "Acute myocardial infarction, unspecified"
             },
             "keluhan": "",
             "noKunjungan": "0304R0050217A000079",
             "pelayanan": {
                "kode": "1",
                "nama": "Rawat Inap"
             },
             "peserta": {
                "cob": {
                   "nmAsuransi": null,
                   "noAsuransi": null,
                   "tglTAT": null,
                   "tglTMT": null
                },
                "hakKelas": {
                   "keterangan": "KELAS III",
                   "kode": "3"
                },
                "informasi": {
                   "dinsos": null,
                   "noSKTM": null,
                   "prolanisPRB": null
                },
                "jenisPeserta": {
                   "keterangan": "PBI (APBN)",
                   "kode": "21"
                },
                "mr": {
                   "noMR": "971430",
                   "noTelepon": null
                },
                "nama": "MUHAMMAD JUSAR",
                "nik": "1106081301530001",
                "noKartu": "0105986780439",
                "pisa": "1",
                "provUmum": {
                   "kdProvider": "03050301",
                   "nmProvider": "BASO"
                },
                "sex": "L",
                "statusPeserta": {
                   "keterangan": "AKTIF",
                   "kode": "0"
                },
                "tglCetakKartu": "2017-11-13",
                "tglLahir": "1953-07-01",
                "tglTAT": "2053-07-01",
                "tglTMT": "2013-01-01",
                "umur": {
                   "umurSaatPelayanan": "63 tahun ,7 bulan ,23 hari",
                   "umurSekarang": "64 tahun ,4 bulan ,12 hari"
                }
             },
             "poliRujukan": {
                "kode": "",
                "nama": ""
             },
             "provPerujuk": {
                "kode": "0304R005",
                "nama": "RSI IBNU SINA"
             },
             "tglKunjungan": "2017-02-24"
          }
       }
    }