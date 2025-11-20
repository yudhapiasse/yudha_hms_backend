{Base URL}/{Service Name}/dashboard/waktutunggu/tanggal/{Parameter1}/waktu/{Parameter2}
Fungsi : Dashboard waktu per tanggal

Method : GET

Format : Json

Header :
x-cons-id: {cons id akses}
x-timestamp: {timestamp akses}
x-signature: {signature akses}
user_key: {userkey akses}

Parameter1 : {diisi tanggal}=> 2021-04-16

Parameter2 : {diisi waktu}=> rs atau server


    {
        "metadata": {
            "code": 200,
            "message": "OK"
        },
        "response": {
            "list": [
                {
                    "kdppk": "1311R002",
                    "waktu_task1": 0,
                    "avg_waktu_task4": 0,
                    "jumlah_antrean": 1,
                    "avg_waktu_task3": 0,
                    "namapoli": "BEDAH",
                    "avg_waktu_task6": 0,
                    "avg_waktu_task5": 0,
                    "nmppk": "RSU AISYIYAH",
                    "avg_waktu_task2": 0,
                    "avg_waktu_task1": 0,
                    "kodepoli": "BED",
                    "waktu_task5": 0,
                    "waktu_task4": 0,
                    "waktu_task3": 0,
                    "insertdate": 1627873951000,
                    "tanggal": "2021-04-16",
                    "waktu_task2": 0,
                    "waktu_task6": 0
                }
            ]
        }
    }


Catatan:

1. Waktu Task 1 = Waktu tunggu admisi dalam detik
2. Waktu Task 2 = Waktu layan admisi dalam detik
3. Waktu Task 3 = Waktu tunggu poli dalam detik
4. Waktu Task 4 = Waktu layan poli dalam detik
5. Waktu Task 5 = Waktu tunggu farmasi dalam detik
6. Waktu Task 6 = Waktu layan farmasi dalam detik
7. Insertdate = Waktu pengambilan data, timestamp dalam milisecond
8. Waktu server adalah data waktu (task 1-6) yang dicatat oleh server BPJS Kesehatan setelah RS mengimkan data, sedangkan waktu rs adalah data waktu (task 1-6) yang dikirimkan oleh RS