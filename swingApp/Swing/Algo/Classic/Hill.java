package Algo.Classic;

import java.util.Random;

public class Hill {


    public String encryptText(String inputText, int[][] key) {
        int size = key.length;
        inputText = inputText.toUpperCase().replaceAll("[^A-Z]", ""); // Remove invalid characters
        while (inputText.length() % size != 0) {
            inputText += "X";
        }

        StringBuilder encryptedText = new StringBuilder();

        for (int i = 0; i < inputText.length(); i += size) {
            int[] block = new int[size];
            for (int j = 0; j < size; j++) {
                block[j] = inputText.charAt(i + j) - 'A';
            }

            int[] encryptedBlock = new int[size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    encryptedBlock[row] += key[row][col] * block[col];
                }
                encryptedBlock[row] %= 26;
            }

            for (int value : encryptedBlock) {
                encryptedText.append((char) (value + 'A'));
            }
        }
        return encryptedText.toString();
    }


    public String decryptText(String encryptedText, int[][] key) {
        int size = key.length;
        int[][] inverseKey = calculateInverseKey(key);

        StringBuilder decryptedText = new StringBuilder();

        for (int i = 0; i < encryptedText.length(); i += size) {
            int[] block = new int[size];
            for (int j = 0; j < size; j++) {
                block[j] = encryptedText.charAt(i + j) - 'A';
            }

            int[] decryptedBlock = new int[size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    decryptedBlock[row] += inverseKey[row][col] * block[col];
                }
                decryptedBlock[row] = (decryptedBlock[row] + 26) % 26; // Modulo 26
            }

            for (int value : decryptedBlock) {
                decryptedText.append((char) (value + 'A'));
            }
        }

        return decryptedText.toString().replaceAll("X+$", ""); // Remove padding
    }

    public int[][] genKey(int size) {
        Random random = new Random();
        int[][] key = new int[size][size];
        do {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    key[i][j] = random.nextInt(26);
                }
            }
        } while (!isInvertible(key));
        return key;
    }
    public int[][] loadKey(String keyString, int size) {
        int[][] key = new int[size][size];
        String[] values = keyString.split(" ");
        if (values.length != size * size) {
            throw new IllegalArgumentException("Invalid key size");
        }
        int index = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                key[i][j] = Integer.parseInt(values[index++]);
            }
        }
        if (!isInvertible(key)) {
            throw new IllegalArgumentException("Key is not invertible");
        }
        return key;
    }

    private boolean isInvertible(int[][] matrix) {
        int det = calculateDeterminant(matrix) % 26;
        return det != 0 && gcd(det, 26) == 1;
    }
    private int calculateDeterminant(int[][] matrix) {
        if (matrix.length == 2) {
            return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
        } else if (matrix.length == 3) {
            return matrix[0][0] * (matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1])
                    - matrix[0][1] * (matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0])
                    + matrix[0][2] * (matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0]);
        }
        throw new UnsupportedOperationException("Determinant calculation for size > 3 not implemented");
    }
    private int[][] calculateInverseKey(int[][] key) {
        int size = key.length;
        int det = calculateDeterminant(key) % 26;
        det = (det + 26) % 26;
        int detInverse = modInverse(det, 26);

        int[][] inverseKey = new int[size][size];
        if (size == 2) {
            inverseKey[0][0] = key[1][1] * detInverse % 26;
            inverseKey[0][1] = (-key[0][1] + 26) * detInverse % 26;
            inverseKey[1][0] = (-key[1][0] + 26) * detInverse % 26;
            inverseKey[1][1] = key[0][0] * detInverse % 26;
        } else if (size == 3) {
            int[][] adj = calculateAdjoint(key);
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    inverseKey[i][j] = adj[i][j] * detInverse % 26;
                    if (inverseKey[i][j] < 0) {
                        inverseKey[i][j] += 26;
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Inverse calculation for size > 3 not implemented");
        }
        return inverseKey;
    }
    private int[][] calculateAdjoint(int[][] matrix) {
        int[][] adj = new int[3][3];
        adj[0][0] = matrix[1][1] * matrix[2][2] - matrix[1][2] * matrix[2][1];
        adj[0][1] = -(matrix[1][0] * matrix[2][2] - matrix[1][2] * matrix[2][0]);
        adj[0][2] = matrix[1][0] * matrix[2][1] - matrix[1][1] * matrix[2][0];
        adj[1][0] = -(matrix[0][1] * matrix[2][2] - matrix[0][2] * matrix[2][1]);
        adj[1][1] = matrix[0][0] * matrix[2][2] - matrix[0][2] * matrix[2][0];
        adj[1][2] = -(matrix[0][0] * matrix[2][1] - matrix[0][1] * matrix[2][0]);
        adj[2][0] = matrix[0][1] * matrix[1][2] - matrix[0][2] * matrix[1][1];
        adj[2][1] = -(matrix[0][0] * matrix[1][2] - matrix[0][2] * matrix[1][0]);
        adj[2][2] = matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];

        int[][] adjT = new int[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                adjT[i][j] = adj[j][i];
            }
        }
        return adjT;
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
    private int modInverse(int a, int m) {
        for (int x = 1; x < m; x++) {
            if ((a * x) % m == 1) {
                return x;
            }
        }
        throw new ArithmeticException("No modular inverse exists");
    }
}
