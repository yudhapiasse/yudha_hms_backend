{Base URL}/{Service Name}/sep/KllInduk/List/{Parameter 1}
Fungsi : Pencarian data SEP Induk Kecelakaan Lalu Lintas

Method : GET

Format : Json

Content-Type: application/json; charset=utf-8

Parameter 1 : No.Kartu Peserta


                    {
                        "metaData": {
                            "code": "200",
                            "message": "Ok"
                        },
                        "response": {
                            "list": [
                                {
                                    "noSEP": "0301R0110421V000439",
                                    "tglKejadian": "2021-04-16",
                                    "ppkPelSEP": "0301R011",
                                    "kdProp": "14",
                                    "kdKab": "0200",
                                    "kdKec": "6122",
                                    "ketKejadian": "kll",
                                    "noSEPSuplesi": "0301R0110421V000435, 0301R0110421V000436, 0301R0110421V000437, 0301R0110421V000438"
                                },
                                {
                                    "noSEP": "1111R0010421V001672",
                                    "tglKejadian": "2021-04-14",
                                    "ppkPelSEP": "1111R001",
                                    "kdProp": "10",
                                    "kdKab": "0115",
                                    "kdKec": "1192",
                                    "ketKejadian": "KLL",
                                    "noSEPSuplesi": null
                                }
                            ]
                        }
                    }
                                                        
                    