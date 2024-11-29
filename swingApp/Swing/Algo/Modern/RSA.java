package Algo.Modern;

import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSA {
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private static final String RSA_ALGORITHM = "RSA";


    public RSA() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(2048); // Độ dài khóa
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        this.publicKey = keyPair.getPublic();
        this.privateKey = keyPair.getPrivate();
    }


    public RSA(String publicKeyBase64, String privateKeyBase64) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

        if (publicKeyBase64 != null) {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            this.publicKey = keyFactory.generatePublic(new java.security.spec.X509EncodedKeySpec(publicKeyBytes));
        }

        if (privateKeyBase64 != null) {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            this.privateKey = keyFactory.generatePrivate(new java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes));
        }
    }


    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes); // Trả về dạng Base64
    }


    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes); // Trả về chuỗi gốc
    }


    public String getPublicKeyBase64() {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }


    public String getPrivateKeyBase64() {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Test Class
    public static void main(String[] args) {
        try {
            // 1. Tạo cặp khóa mới
            RSA rsa = new RSA();
            System.out.println("Public Key (Base64): " + rsa.getPublicKeyBase64());
            System.out.println("Private Key (Base64): " + rsa.getPrivateKeyBase64());

            // 2. Mã hóa và giải mã
            String originalMessage = "Hello, RSA!";
            System.out.println("\nOriginal Message: " + originalMessage);

            String encryptedMessage = rsa.encrypt(originalMessage);
            System.out.println("Encrypted Message: " + encryptedMessage);

            String decryptedMessage = rsa.decrypt(encryptedMessage);
            System.out.println("Decrypted Message: " + decryptedMessage);

            // 3. Nhập cặp khóa để sử dụng lại
            System.out.println("\nUsing existing keys:");
            RSA rsaWithExistingKeys = new RSA(rsa.getPublicKeyBase64(), rsa.getPrivateKeyBase64());
            String encryptedWithExistingKeys = rsaWithExistingKeys.encrypt(originalMessage);
            System.out.println("Encrypted with existing keys: " + encryptedWithExistingKeys);

            String decryptedWithExistingKeys = rsaWithExistingKeys.decrypt(encryptedWithExistingKeys);
            System.out.println("Decrypted with existing keys: " + decryptedWithExistingKeys);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
