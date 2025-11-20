Create Signature
Overview

Secara umum, hampir setiap pemanggilan web-service, harus dicantumkan beberapa variabel yang dibutuhkan untuk menambahkan informasi ataupun untuk proses validasi yang dikirim pada HTTP Header, antara lain:

#	Nama Header	Nilai	Keterangan
1	X-cons-id	743627386	consumer ID dari BPJS Kesehatan
2	X-timestamp	234234234	generated unix-based timestamp
3	X-signature	DogC5UiQurNcigrBdQ3QN5oYvXeUF5E82I/LHUcI9v0=	generated signature dengan pola HMAC-256
4	user_key	d795b04f4a72d74fae727be9da0xxxxx	user_key untuk akses webservice

1. X-cons-id, merupakan kode consumer (pengakses web-service). Kode ini akan diberikan oleh BPJS Kesehatan.

2. X-timestamp, merupakan waktu yang akan di-generate oleh client saat ingin memanggil setiap service. Format waktu ini ditulis dengan format unix-based-time (berisi angka, tidak dalam format tanggal sebagaimana mestinya). Format waktu menggunakan Coordinated Universal Time ( UTC), dalam penggunaannya untuk mendapatkan timestamp, rumus yang digunakan adalah (local time in UTC timezone in seconds) - (1970-01-01 in seconds).

3. X-signature, merupakan hasil dari pembuatan signature yang dibuat oleh client. Signature yang digunakan menggunakan pola HMAC-SHA256.

4. user_key, merupakan key untuk mengakses webservice. Setiap service consumer memiliki user_key masing-masing.



Untuk dapat mengakses web-service dari BPJS Kesehatan (service provider), pemanggil web service (service consumer) akan mendapatkan:
• Consumer ID
• Consumer Secret


Informasi Consumer Secret, hanya disimpan oleh service consumer. Tidak dikirim ke server web-service, hal ini untuk menjaga pengamanan yang lebih baik. Sedangkan kebutuhan Consumer Secret ini adalah untuk men-generate Signature (X-signature).

Contoh:
consumerID : 1234
consumerSecret : pwd
timestamp : 433223232
variabel1 : consumerID&timestamp
variabel1 : 1234&433223232
SIGNATURE

Metode signature yang digunakan adalah menggunakan HMAC-SHA256, dimana paramater saat generate signature dibutuhkan parameter message dan key.
Berikut contoh hasil generate HMAC-SHA256
message : aaa
key : bbb
hasil generate HMAC-SHA256 : 20BKS3PWnD3XU4JbSSZvVlGi2WWnDa8Sv9uHJ+wsELA=
Diatas adalah hasil generate dari server BPJS Kesehatan

Signature : HMAC-256(value : key)
value : variabel1
key : consumerSecret
Signature : HMAC-256(variabel1 : consumerSecret)

Contoh Pembuatan Signature

JAVA
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

    import javax.crypto.Mac;
    import javax.crypto.spec.SecretKeySpec;
 
    import org.springframework.security.crypto.codec.Base64;
 
    public class BpjsApi {
 
        public static void main(String[] args) throws GeneralSecurityException, IOException {
 
            String secretKey = "secretKey";
            String salt = "0123456789";
 
            String generateHmacSHA256Signature = generateHmacSHA256Signature(salt, secretKey);
            System.out.println("Signature: " + generateHmacSHA256Signature);
 
            String urlEncodedSign = URLEncoder.encode(generateHmacSHA256Signature, "UTF-8");
 
            System.out.println("Url encoded value: " + urlEncodedSign);
        }
 
        public static String generateHmacSHA256Signature(String data, String key) throws GeneralSecurityException {
            byte[] hmacData = null;
 
            try {
                SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(secretKey);
                hmacData = mac.doFinal(data.getBytes("UTF-8"));
                return new Base64Encoder().encode(hmacData);
            } catch (UnsupportedEncodingException e) {
                throw new GeneralSecurityException(e);
            }
        }
    }

© 2022 BPJS Kesehatan.BPJS Kesehatan Trust Mark Versi 1.0.0