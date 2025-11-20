{BASE URL}/{Service Name}/RencanaKontrol/nosep/{parameter}
Fungsi : Melihat data SEP untuk keperluan rencana kontrol

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter: Nomor SEP Peserta



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
           },
        "response": {
            "noSep": "0301R0010819V006059",
            "tglSep": "2019-10-17",
            "jnsPelayanan": "Rawat Jalan",
            "poli": "HDL - HEMODIALISA",
            "diagnosa": "Z49.1 - Extracorporeal dialysis",
            "peserta": {
            "noKartu": "0000018965349",
            "nama": "RASBEN",
            "tglLahir": "1957-11-10",
            "kelamin": "L",
            "hakKelas": "-"
        },
        "provUmum": {
            "kdProvider": "03100202",
            "nmProvider": "KAMPUNG TELENG"
        },
        "provPerujuk": {
            "kdProviderPerujuk": "03100202",
            "nmProviderPerujuk": "KAMPUNG TELENG",
            "asalRujukan": "1",
            "noRujukan": "031002020619P000413",
            "tglRujukan": "2019-10-17"
            }
        }
    }
      