package Algo.Classic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CaesarCipher {
    public static String encryptText(String text, int shift) {
        StringBuilder encryptedText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (Character.isLetter(ch)) {
                char base = (Character.isLowerCase(ch)) ? 'a' : 'A';
                ch = (char) ((ch - base + shift) % 26 + base);
            }
            encryptedText.append(ch);
        }
        return encryptedText.toString();
    }

    public static String decryptText(String text, int shift) {
        return encryptText(text, 26 - shift); // Decrypting is the inverse of encryption
    }

    public static void encryptFile(String inputFilePath, String outputFilePath, int shift) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        String encryptedContent = encryptText(fileContent, shift);
        Files.write(Paths.get(outputFilePath), encryptedContent.getBytes());
    }
    public static void decryptFile(String inputFilePath, String outputFilePath, int shift) throws IOException {
        String fileContent = new String(Files.readAllBytes(Paths.get(inputFilePath)));
        String decryptedContent = decryptText(fileContent, shift);
        Files.write(Paths.get(outputFilePath), decryptedContent.getBytes());
    }
    public static int getKey() {
        return (int) (Math.random() * 25) + 1;
    }

    public static String applyPadding(String text, int blockSize) {
        int paddingLength = blockSize - (text.length() % blockSize);
        if (paddingLength == blockSize) {
            return text; // No padding needed
        }
        return text + " ".repeat(paddingLength);
    }

}
