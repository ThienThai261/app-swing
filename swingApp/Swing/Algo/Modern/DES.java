package Algo.Modern;

import Other.CipherAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class DES implements CipherAlgorithm {
    private SecretKey secretKey;
    private static final String ALGORITHM = "DES";
    private String mode = "ECB";
    private String padding = "PKCS5Padding";
    private IvParameterSpec ivParameterSpec;

    public void setMode(String mode) {
        this.mode = mode;
        System.out.println("Mode set to: " + this.mode);
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
        byte[] iv = new byte[8];
        new SecureRandom().nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
        return iv;
    }

    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(keySize);
        this.secretKey = keyGen.generateKey();
        return this.secretKey;
    }

    @Override
    public void loadKey(byte[] keyData) {
        this.secretKey = new SecretKeySpec(keyData, ALGORITHM);
    }

    @Override
    public String encrypt(String text, String key, String padding, String mode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);

        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);


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
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);

        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }

        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);

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
    public void encryptFile(File file, String loadedKey, String padding, String mode) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), ALGORITHM);
        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }
        Cipher cipher = Cipher.getInstance(ALGORITHM + "/" + mode + "/" + padding);


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


        File encryptedFile = new File(file.getParent(), file.getName() + ".enc");
        Files.write(encryptedFile.toPath(), encryptedBase64.getBytes(StandardCharsets.UTF_8));

        System.out.println("File encrypted successfully. Encrypted file saved at: " + encryptedFile.getAbsolutePath());
    }

    @Override
    public void decryptFile(File file, String loadedKey, String padding, String mode)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IOException, IllegalBlockSizeException, BadPaddingException, Exception {


        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "DES");


        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }


        Cipher cipher = Cipher.getInstance("DES/" + mode + "/" + padding);


        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                throw new IllegalStateException("IV is required but not set for " + mode + " mode.");
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        }


        String encryptedBase64 = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);


        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);


        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);


        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);


        File decryptedFile = new File(file.getParent(), file.getName().replace(".enc", ".dec"));
        Files.write(decryptedFile.toPath(), decryptedText.getBytes(StandardCharsets.UTF_8));

        System.out.println("File decrypted successfully. Decrypted file saved at: " + decryptedFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        try {

            DES des = new DES();
            SecretKey key = des.genKey(56);
            String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
            System.out.println("Generated Key: " + base64Key);

            String mode = "CBC";
            String padding = "PKCS5Padding";


            File inputFile = new File("C:\\Users\\Asus\\Desktop\\testCEa\\file.txt"); // Input file to encrypt
            File encryptedFile = new File("C:\\Users\\Asus\\Desktop\\testCEa\\file.txt.enc"); // Output encrypted file

            des.encryptFile(inputFile, base64Key, padding, mode);
            System.out.println("File encrypted successfully!");

            des.decryptFile(encryptedFile, base64Key, padding, mode);
            System.out.println("File decrypted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
