package Algo.Classic;

import java.util.*;

public class SubstitutionCipher {

    public static String generateRandomSubstitutionKey() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        List<Character> chars = new ArrayList<>();
        for (char c : alphabet.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);

        StringBuilder key = new StringBuilder();
        for (char c : chars) {
            key.append(c);
        }
        return key.toString();
    }

    public static String encrypt(String text, String key) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        text = text.toUpperCase().replaceAll("[^A-Z]", "");

        StringBuilder encryptedText = new StringBuilder();

        for (char c : text.toCharArray()) {
            int index = alphabet.indexOf(c);
            encryptedText.append(key.charAt(index));
        }

        return encryptedText.toString();
    }


    public static String decrypt(String encryptedText, String key) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        StringBuilder decryptedText = new StringBuilder();

        for (char c : encryptedText.toCharArray()) {
            int index = key.indexOf(c);
            decryptedText.append(alphabet.charAt(index));
        }

        return decryptedText.toString();
    }

}
