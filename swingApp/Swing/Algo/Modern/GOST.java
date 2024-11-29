package Algo.Modern;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Base64;

public class GOST {
    SecretKey secretKey;
    private String mode = "ECB";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    SecretKey key;

    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        return genKeyGost();
    }

    public SecretKey genKeyGost() throws NoSuchAlgorithmException {
        try {

            KeyGenerator keyGenerator = KeyGenerator.getInstance("GOST28147", "BC");


            keyGenerator.init(256);


            key = keyGenerator.generateKey();
            return key;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new NoSuchAlgorithmException("Error generating GOST28147 key", e);
        }
    }

    public void setMode(String mode) {
        this.mode = mode;
    }


    public SecretKey loadKey(byte[] keyData) {

        return this.secretKey = new SecretKeySpec(keyData, "AES");
    }

    public byte[] genIV() {
        return new byte[8]; // GOSTFALSE uses a 64-bit IV (8 bytes)
    }

    public void setIV(byte[] ivData) {

    }


    public static String encryptText(String plainText, Key key, String mode, String padding) throws Exception {
        Cipher cipher;
        if (mode.equalsIgnoreCase("ECB")) {

            cipher = Cipher.getInstance("GOST28147/" + mode + "/" + "PKCS5Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {

            cipher = Cipher.getInstance("GOST28147/" + mode + "/" + padding, "BC");
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[8]); // 64-bit IV
            cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        }
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }


    public static String decryptText(String cipherText, Key key, String mode, String padding) throws Exception {
        Cipher cipher;
        if (mode.equalsIgnoreCase("ECB")) {

            cipher = Cipher.getInstance("GOST28147/" + mode + "/" + padding, "BC");
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {

            cipher = Cipher.getInstance("GOST28147/" + mode + "/" + padding, "BC");
            IvParameterSpec ivSpec = new IvParameterSpec(new byte[8]); // 64-bit IV
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        }
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
        return new String(decrypted);
    }


    public static void encryptFile(String inputFile, String outputFile, Key key, String mode, String padding) throws Exception {
        processFile(Cipher.ENCRYPT_MODE, inputFile, outputFile, key, mode, padding);
    }


    public static void decryptFile(String inputFile, String outputFile, Key key, String mode, String padding) throws Exception {
        processFile(Cipher.DECRYPT_MODE, inputFile, outputFile, key, mode, padding);
    }


    private static void processFile(int cipherMode, String inputFile, String outputFile, Key key, String mode, String padding) throws Exception {
        Cipher cipher = Cipher.getInstance("GOST28147/" + mode + "/" + padding, "BC");


        IvParameterSpec ivSpec = null;
        if (!mode.equalsIgnoreCase("ECB")) {
            ivSpec = new IvParameterSpec(new byte[8]);
        }


        if (ivSpec != null) {
            cipher.init(cipherMode, key, ivSpec);
        } else {
            cipher.init(cipherMode, key); // No IV needed for ECB
        }

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {

            byte[] buffer = new byte[64]; // 64-bit block size for GOST
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(cipher.update(buffer, 0, bytesRead));
            }
            fos.write(cipher.doFinal());
        }
    }

    public static void main(String[] args) {
        try {
            GOST gost = new GOST();


            SecretKey key = gost.genKey(256);
            byte[] keyBytes = key.getEncoded();
            int keySize = keyBytes.length;

            System.out.println("Generated Key Size: " + keySize + " bytes");


            String plainText = "Hello!";

            String cipherText = encryptText(plainText, key, "ECB", "PKCS5Padding");
            System.out.println("Encrypted Text: " + cipherText);

            String decryptedText = decryptText(cipherText, key, "ECB", "PKCS5Padding");
            System.out.println("Decrypted Text: " + decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
