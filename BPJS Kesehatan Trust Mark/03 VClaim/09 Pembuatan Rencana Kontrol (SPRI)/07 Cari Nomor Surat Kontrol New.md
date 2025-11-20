{BASE URL}/{Service Name}/RencanaKontrol/noSuratKontrol/{parameter}
Fungsi : Melihat data SEP untuk keperluan rencana kontrol

Method : GET

Format : Json

Content-Type: Application/x-www-form-urlencoded

Parameter: Nomor Surat Kontrol Peserta



    {
        "metaData": {
            "code": "200",
            "message": "Sukses"
        },
        "response": {
            "noSuratKontrol": "0301R0010120K000003",
            "tglRencanaKontrol": "2020-01-21",
            "tglTerbit": "2020-01-21",
            "jnsKontrol": "2",
            "poliTujuan": "010",
            "namaPoliTujuan": "ENDOKRIN-METABOLIK-DIABETES",
            "kodeDokter": "266822",
            "namaDokter": "DR.dr.H Eva Decroli, SpPD K-EMD Finasim",
            "flagKontrol": "False",
            "kodeDokterPembuat": null,
            "namaDokterPembuat": null,
            "namaJnsKontrol": "Kontrol",
            "sep": {
                "noSep": "0301R0010819V005647",
                "tglSep": "2020-01-18",
                "jnsPelayanan": "Rawat Jalan",
                "poli": "010 - ENDOKRIN-METABOLIK-DIABETES",
                "diagnosa": "E11 - Non-insulin-dependent diabetes mellitus",
                "peserta": {
                "noKartu": "0000015450401",
                "nama": "PIASDIL",
                "tglLahir": "1954-04-12",
                "kelamin": "L",
                "hakKelas": "-"
                },
            "provUmum": {
                "kdProvider": "03030101",
                "nmProvider": "TARUSAN"
                },
            "provPerujuk": {
                "kdProviderPerujuk": "0042R007",
                "nmProviderPerujuk": "Rumah Sakit BKM Painan",
                "asalRujukan": "2",
                "noRujukan": "0042R0070819B000072",
                "tglRujukan": "2020-01-18"
                }
            }
        }
    }

Catatan:
Ketika pembuatan SPRI atau jenis kontrol 1 tidak ada referensi nomor SEP asalnya, jadi field response SEP kosong atau null.
Sedangkan jika pembuatan surat kontrol atau jenis kontrol 2, akan terisi field response SEP karena terdapat referensi nomor SEP asal ketika pembuatan surat kontrol tersebut.
                                         
    