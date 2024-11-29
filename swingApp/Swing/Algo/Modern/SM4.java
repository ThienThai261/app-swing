package Algo.Modern;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

public class SM4 {

    static {

        Security.addProvider(new BouncyCastleProvider());
    }

    private String mode = "ECB"; // Chế độ mặc định
    private String padding = "PKCS5Padding"; // Padding mặc định
    private SecretKey secretKey;
    private IvParameterSpec ivParameterSpec;


    // Constructor
    public SM4() {
        this.ivParameterSpec = new IvParameterSpec(new byte[16]);
    }

    // Cấu hình chế độ và padding
    public void setModeAndPadding(String mode, String padding) {
        this.mode = mode;
        this.padding = padding;
    }

    // Tạo khóa ngẫu nhiên
    public void genKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("SM4", "BC");
        keyGen.init(128);  // SM4 requires a 128-bit key
        this.secretKey = keyGen.generateKey();

        // Print the length of the generated key
        System.out.println("Key length: " + this.secretKey.getEncoded().length + " bytes");
    }


    // Tải khóa từ byte array
    public void loadKey(byte[] keyBytes) {
        this.secretKey = new SecretKeySpec(keyBytes, "SM4");
    }

    // Lấy khóa hiện tại dưới dạng Base64
    public String getEncodedKey() {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    // Phương thức mã hóa văn bản
    public String encryptText(String plaintext) throws Exception {
        if (requiresIV() && ivParameterSpec == null) {
            throw new Exception("Please set IV before using this mode.");
        }
        Cipher cipher = Cipher.getInstance("SM4/" + mode + "/" + padding, "BC");
        if (requiresIV()) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Phương thức giải mã văn bản
    public String decryptText(String ciphertext) throws Exception {
        if (requiresIV() && ivParameterSpec == null) {
            throw new Exception("Please set IV before using this mode.");
        }
        Cipher cipher = Cipher.getInstance("SM4/" + mode + "/" + padding, "BC");
        if (requiresIV()) {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    public boolean requiresIV() {
        return mode.equals("CBC") || mode.equals("CFB") || mode.equals("OFB") || mode.equals("CTR");
    }

    // Mã hóa file
    public void encryptFile(File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/" + mode + "/" + padding, "BC");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            processFile(cipher, fis, fos);
        }
    }

    // Giải mã file
    public void decryptFile(File inputFile, File outputFile) throws Exception {
        Cipher cipher = Cipher.getInstance("SM4/" + mode + "/" + padding, "BC");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        try (FileInputStream fis = new FileInputStream(inputFile);
             FileOutputStream fos = new FileOutputStream(outputFile)) {
            processFile(cipher, fis, fos);
        }
    }

    // Xử lý file mã hóa/giải mã
    private void processFile(Cipher cipher, InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                out.write(output);
            }
        }
        byte[] output = cipher.doFinal();
        if (output != null) {
            out.write(output);
        }
    }

    public byte[] genIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        this.ivParameterSpec = new IvParameterSpec(iv);
        return iv;
    }

    public void loadIV(byte[] iv) throws Exception {
        if (iv.length != 16) {
            throw new Exception("Invalid IV length. IV must be 16 bytes.");
        }
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    // Đặt lại IV mới

    public void setIV(byte[] iv) throws Exception {
        if (iv.length != 16) {
            throw new Exception("Invalid IV length. SM4 requires a 128-bit (16-byte) IV.");
        }
        this.ivParameterSpec = new IvParameterSpec(iv);
    }

    public void setKey(byte[] key) throws InvalidKeyException {
        if (key.length != 16) {
            throw new InvalidKeyException("Invalid key length. SM4 requires a 128-bit (16-byte) key.");
        }
        this.secretKey = new SecretKeySpec(key, "SM4");  // Set the secret key for SM4
    }


    public static void main(String[] args) {
        try {
            SM4 sm4 = new SM4();
            sm4.setModeAndPadding("CFB", "NoPadding");

            // Tạo khóa
            sm4.genKey();
            System.out.println("Generated Key (Base64): " + sm4.getEncodedKey());

            // Mã hóa và giải mã văn bản
            String plaintext = "Hello, SM4 Encryption!";
            String ciphertext = sm4.encryptText(plaintext);
            System.out.println("Ciphertext: " + ciphertext);
            String decryptedText = sm4.decryptText(ciphertext);
            System.out.println("Decrypted Text: " + decryptedText);

            // Mã hóa và giải mã file
            File inputFile = new File("input.txt");
            File encryptedFile = new File("encrypted.dat");
            File decryptedFile = new File("decrypted.txt");

            sm4.encryptFile(inputFile, encryptedFile);
            System.out.println("File encrypted successfully!");

            sm4.decryptFile(encryptedFile, decryptedFile);
            System.out.println("File decrypted successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMode(String newMode) throws Exception {
        // Kiểm tra xem chế độ có hợp lệ không
        if (!newMode.equals("ECB") && !newMode.equals("CBC") && !newMode.equals("CFB") &&
                !newMode.equals("OFB") && !newMode.equals("CTR")) {
            throw new Exception("Unsupported mode: " + newMode);
        }
        this.mode = newMode;
        // Nếu cần, cập nhật cấu hình liên quan đến chế độ mới
    }

    public String getMode() {
        return this.mode;
    }
}



