package Algo.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Hasher {

    public static String hash(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
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
            String hash = SHA256Hasher.hash(text);
            System.out.println("SHA-256 Hash: " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
