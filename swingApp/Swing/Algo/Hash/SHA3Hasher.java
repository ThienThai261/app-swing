package Algo.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA3Hasher {

    // Phương thức để băm một chuỗi sử dụng thuật toán SHA3 (SHA-3)
    public static String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA3-256");
        byte[] hashBytes = digest.digest(input.getBytes());
        return bytesToHex(hashBytes);
    }

    private static String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static void main(String[] args) {
        String text = "Hello, World!";
        try {
            String hash = SHA3Hasher.hash(text);
            System.out.println("SHA-3 Hash: " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
