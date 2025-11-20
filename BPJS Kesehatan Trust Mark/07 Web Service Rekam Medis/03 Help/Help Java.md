JAVA
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

    public class Main {

      public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
          return str;
        }/*w  w w. ja  va 2s.c om*/
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes());
        gzip.close();
        return out.toString("ISO-8859-1");
      }

    }

Contoh Pembuatan Enkripsi AES

Java
public string Encrypt(string consid, string conspwd, string kodefaskes, string data)
{

            string key = consid + secretkey + kodefaskes;

            string encData = null;
            byte[][] keys = GetHashKeys(key);

            try
            {
                encData = EncryptStringToBytes_Aes(data, keys[0], keys[1]);
            }
            catch (CryptographicException) { }
            catch (ArgumentNullException) { }

            return encData;
        }