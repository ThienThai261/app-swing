package Algo.Modern;

import Other.CipherAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AES implements CipherAlgorithm {
    private SecretKey secretKey;
    private String mode = "ECB";
    private String padding = "PKCS5Padding";
    private IvParameterSpec ivParameterSpec;

    public void setMode(String mode) {
        this.mode = mode;
        System.out.println("Mode set to: " + this.mode); // Debug output
    }

    public String getMode() {
        return mode;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public void setIV(byte[] iv) {
        ivParameterSpec = new IvParameterSpec(iv);
    }

    @Override
    public byte[] genIV() {
        byte[] iv = new byte[16]; // AES block size là 16 bytes
        new SecureRandom().nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
        return iv;
    }

    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        this.secretKey = keyGen.generateKey();
        return this.secretKey;
    }

    @Override
    public void loadKey(byte[] keyData) {
        this.secretKey = new SecretKeySpec(keyData, "AES");
    }

    @Override
    public String encrypt(String text, String key, String padding, String mode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }

        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);


        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                genIV();
            }
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        }

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String encryptedText, String key, String padding, String mode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "AES");

        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding"; // Java uses PKCS5 for PKCS7
        }

        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);

        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                throw new IllegalStateException("IV is required but not set for " + mode + " mode.");
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        }

        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @Override
    public void encryptFile(File file, String loadedKey, String padding, String mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IOException, IllegalBlockSizeException, BadPaddingException, Exception {


        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "AES");


        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }


        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);


        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                genIV();
            }
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        }
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] encryptedBytes = cipher.doFinal(fileBytes);
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);
        saveFile(file, encryptedBase64.getBytes(StandardCharsets.UTF_8), "Encrypted");
    }


    @Override

    public void decryptFile(File file, String loadedKey, String padding, String mode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "AES");
        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }


        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);


        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                throw new IllegalStateException("IV is required but not set for " + mode + " mode.");
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        }


        byte[] encodedData = Files.readAllBytes(file.toPath());
        byte[] decodedData = Base64.getDecoder().decode(encodedData);


        byte[] decryptedData = cipher.doFinal(decodedData);


        saveFile(file, decryptedData, "Decrypted");
    }


    // lưu vào file sau khi mã hóa và giải mã
    private void saveFile(File originalFile, byte[] content, String type) throws IOException {
        File outputFile = new File(originalFile.getParent(), originalFile.getName() + "." + type.toLowerCase());
        Files.write(outputFile.toPath(), content);
        System.out.println(type + " file saved at: " + outputFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        try {
            AES aes = new AES();
            aes.setMode("ECB");
            aes.setPadding("PKCS5Padding");
            String key = Base64.getEncoder().encodeToString("1234567890123456".getBytes());
            String iv = Base64.getEncoder().encodeToString(aes.genIV());


            String text = "Hello, World!";
            System.out.println("Original Text: " + text);
            String encryptedText = aes.encrypt(text, key, "PKCS5Padding", aes.getMode());
            System.out.println("Encrypted Text: " + encryptedText);
            String decryptedText = aes.decrypt(encryptedText, key, "PKCS5Padding", aes.getMode());
            System.out.println("Decrypted Text: " + decryptedText);
            File testFile = new File("C:\\Users\\Asus\\Desktop\\testCEa\\file.txt");
            aes.encryptFile(testFile, key, "PKCS5Padding", aes.getMode());
            aes.decryptFile(testFile, key, "PKCS5Padding", aes.getMode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
