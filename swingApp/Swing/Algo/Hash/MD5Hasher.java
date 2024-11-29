package Algo.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hasher {

    // Phương thức để băm một chuỗi sử dụng thuật toán MD5
    public static String hash(String input) throws NoSuchAlgorithmException {
        // Tạo đối tượng MessageDigest với thuật toán MD5
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // Chuyển chuỗi đầu vào thành một mảng byte
        byte[] hashBytes = digest.digest(input.getBytes());

        // Chuyển mảng byte thành một chuỗi hex để dễ đọc
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            // Chuyển mỗi byte thành một chuỗi hex 2 ký tự
            String hex = Integer.toHexString(0xff & b);
            // Nếu chuỗi hex có độ dài 1 (ví dụ: 'f'), thêm 0 vào phía trước
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
            // Tính toán và in ra giá trị MD5 của chuỗi nhập vào
            String hash = MD5Hasher.hash(text);
            System.out.println("MD5 Hash của \"" + text + "\" là: " + hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
