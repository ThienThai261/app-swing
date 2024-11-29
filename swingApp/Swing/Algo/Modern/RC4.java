package Algo.Modern;

import Other.CipherAlgorithm;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class RC4 implements CipherAlgorithm {
    private SecretKey secretKey;

    @Override
    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        byte[] keyData = genKeyRC4(keySize);
        return new SecretKeySpec(keyData, "RC4");
    }

    @Override
    public void loadKey(byte[] keyData) {
        this.secretKey = new SecretKeySpec(keyData, "RC4");
    }


    public byte[] genKeyRC4(int keySize) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[keySize / 8]; // Convert bits to bytes
        secureRandom.nextBytes(key);
        return key;
    }

    @Override
    public byte[] genIV() {

        return null;
    }

    @Override
    public void setIV(byte[] ivData) {

    }

    @Override
    public String encrypt(String text, String key, String padding, String mode) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "RC4");

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    @Override
    public String decrypt(String encryptedText, String key, String padding, String mode) throws Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), "RC4");

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    @Override
    public void encryptFile(File file, String loadedKey, String padding, String mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "RC4");

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        byte[] encryptedBytes = cipher.doFinal(fileBytes);


        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        File encryptedFile = new File(file.getParent(), file.getName() + ".enc");
        Files.write(encryptedFile.toPath(), encryptedBase64.getBytes(StandardCharsets.UTF_8));

        System.out.println("File encrypted successfully. Encrypted file saved at: " + encryptedFile.getAbsolutePath());
    }

    @Override
    public void decryptFile(File file, String loadedKey, String padding, String mode)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, Exception {

        SecretKeySpec secretKeySpec = new SecretKeySpec(Base64.getDecoder().decode(loadedKey), "RC4");

        Cipher cipher = Cipher.getInstance("RC4");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

        String encryptedBase64 = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedText = new String(decryptedBytes, StandardCharsets.UTF_8);

        File decryptedFile = new File(file.getParent(), file.getName().replace(".enc", ".dec"));
        Files.write(decryptedFile.toPath(), decryptedText.getBytes(StandardCharsets.UTF_8));

        System.out.println("File decrypted successfully. Decrypted file saved at: " + decryptedFile.getAbsolutePath());
    }
}
