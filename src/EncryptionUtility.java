import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtility {

  private static final String ALGORITHM = "AES";
  private static final Path KEY_FILE_PATH = Paths.get("resources/keyfile.txt");

  public static SecretKey getOrGenerateKey() {
    try {
      if (Files.exists(KEY_FILE_PATH)) {
        String encodedKey = Files.readString(KEY_FILE_PATH);
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, ALGORITHM);
      } else {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        SecretKey secretKey = keyGen.generateKey();
        String encodedKey = Base64
          .getEncoder()
          .encodeToString(secretKey.getEncoded());
        Files.writeString(KEY_FILE_PATH, encodedKey);
        return secretKey;
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      return null;
    }
  }

  public static String encrypt(String input, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encrypted = cipher.doFinal(input.getBytes());
    return Base64.getEncoder().encodeToString(encrypted);
  }

  public static String decrypt(String input, SecretKey key) throws Exception {
    Cipher cipher = Cipher.getInstance(ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] original = cipher.doFinal(Base64.getDecoder().decode(input));
    return new String(original);
  }
}
