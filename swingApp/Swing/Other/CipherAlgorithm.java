package Other;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface CipherAlgorithm {
    SecretKey genKey(int keySize) throws NoSuchAlgorithmException;

    void loadKey(byte[] keyData);

    String encrypt(String text, String key, String padding, String mode) throws Exception;

    String decrypt(String encryptedText, String key, String padding, String mode) throws Exception;

    void encryptFile(File file, String loadedKey,String padding,String mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, Exception;
    byte[] genIV();

    void setIV(byte[] ivData);

    void decryptFile(File fileToDecrypt, String loadedKey, String padding, String mode) throws Exception;
}