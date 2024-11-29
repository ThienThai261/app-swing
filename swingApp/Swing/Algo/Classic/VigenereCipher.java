package Algo.Classic;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class VigenereCipher {

    private String key;

    public void loadKey(String key) {
        this.key = key;
    }

    public String encryptText(String plaintext) {
        StringBuilder encryptedText = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char currentChar = plaintext.charAt(i);
            if (Character.isLetter(currentChar)) {
                char keyChar = key.charAt(keyIndex % key.length());
                int shift = Character.toUpperCase(keyChar) - 'A';
                if (Character.isLowerCase(currentChar)) {
                    encryptedText.append((char) ((currentChar - 'a' + shift) % 26 + 'a'));
                } else {
                    encryptedText.append((char) ((currentChar - 'A' + shift) % 26 + 'A'));
                }
                keyIndex++;
            } else {
                encryptedText.append(currentChar);
            }
        }
        return encryptedText.toString();
    }

    public String decryptText(String ciphertext) {
        StringBuilder decryptedText = new StringBuilder();
        int keyIndex = 0;
        for (int i = 0; i < ciphertext.length(); i++) {
            char currentChar = ciphertext.charAt(i);
            if (Character.isLetter(currentChar)) {
                char keyChar = key.charAt(keyIndex % key.length());
                int shift = Character.toUpperCase(keyChar) - 'A';
                if (Character.isLowerCase(currentChar)) {
                    decryptedText.append((char) ((currentChar - 'a' - shift + 26) % 26 + 'a'));
                } else {
                    decryptedText.append((char) ((currentChar - 'A' - shift + 26) % 26 + 'A'));
                }
                keyIndex++;
            } else {
                decryptedText.append(currentChar);
            }
        }
        return decryptedText.toString();
    }


    public void encryptFile(String inputFile, String outputFile) throws IOException {
        String plaintext = new String(Files.readAllBytes(Path.of(inputFile)));
        String encryptedText = encryptText(plaintext);
        Files.write(Path.of(outputFile), encryptedText.getBytes(), StandardOpenOption.CREATE);
    }

    public void decryptFile(String inputFile, String outputFile) throws IOException {
        String ciphertext = new String(Files.readAllBytes(Path.of(inputFile)));
        String decryptedText = decryptText(ciphertext);
        Files.write(Path.of(outputFile), decryptedText.getBytes(), StandardOpenOption.CREATE);
    }

}

