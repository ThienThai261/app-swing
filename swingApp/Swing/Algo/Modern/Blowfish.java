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

public class Blowfish implements CipherAlgorithm {
    private SecretKey secretKey;
    private String mode = "ECB";
    private String padding = "PKCS5Padding";
    private IvParameterSpec ivParameterSpec;

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public void setIV(byte[] iv) {
        if (iv.length != 8) { // Blowfish block size is 8 bytes
            throw new IllegalArgumentException("IV size must be exactly 8 bytes for Blowfish.");
        }
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    public byte[] genIV() {
        byte[] iv = new byte[8]; // Blowfish block size là 8 bytes
        new SecureRandom().nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
        return iv;
    }

    public SecretKey genKey(int keysize) throws NoSuchAlgorithmException {
        if (keysize < 32 || keysize > 448 || keysize % 8 != 0) {
            throw new IllegalArgumentException("Blowfish key size must be between 32 and 448 bits, and a multiple of 8.");
        }
        KeyGenerator keyGen = KeyGenerator.getInstance("Blowfish");
        keyGen.init(keysize);
        this.secretKey = keyGen.generateKey();
        return this.secretKey;
    }


    @Override
    public void loadKey(byte[] keyData) {
        this.secretKey = new SecretKeySpec(keyData, "Blowfish");
    }

    @Override
    public String encrypt(String text, String key, String padding, String mode) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish/" + mode + "/" + padding);

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
        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "Blowfish");

        Cipher cipher = Cipher.getInstance("Blowfish/" + mode + "/" + padding);

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

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "Blowfish");

        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }


        Cipher cipher = Cipher.getInstance("Blowfish/" + mode + "/" + padding);


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


        File encryptedFile = new File(file.getParent(), file.getName() + ".enc");
        Files.write(encryptedFile.toPath(), encryptedBytes);

        System.out.println("File encrypted successfully. Encrypted file saved at: " + encryptedFile.getAbsolutePath());
    }

    @Override
    public void decryptFile(File file, String loadedKey, String padding, String mode) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "Blowfish");


        if ("PKCS7Padding".equalsIgnoreCase(padding)) {
            padding = "PKCS5Padding";
        }


        System.out.println("Initializing cipher with mode: " + mode + " and padding: " + padding);
        Cipher cipher = Cipher.getInstance("Blowfish/" + mode + "/" + padding);


        if (!"ECB".equalsIgnoreCase(mode)) {
            if (ivParameterSpec == null) {
                throw new IllegalStateException("IV is required but not set for " + mode + " mode.");
            }
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
            System.out.println("Cipher initialized with IV.");
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            System.out.println("Cipher initialized in ECB mode.");
        }


        byte[] encryptedBytes = Files.readAllBytes(file.toPath());

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);


        File decryptedFile = new File(file.getParent(), file.getName().replace(".enc", ".dec"));
        System.out.println("Decrypted file will be saved to: " + decryptedFile.getAbsolutePath());
        Files.write(decryptedFile.toPath(), decryptedBytes);

        System.out.println("File decrypted successfully. Decrypted file saved at: " + decryptedFile.getAbsolutePath());
    }


}
