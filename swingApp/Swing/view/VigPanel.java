package view;

import Algo.Classic.VigenereCipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

public class VigPanel extends JPanel {

    private final VigenereCipher vigenere;
    private JTextField inputKeyField;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;

    public VigPanel() {
        this.vigenere = new VigenereCipher();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new GridLayout(3, 2, 5, 5));
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key Operations"));

        JLabel inputKeyLabel = new JLabel("Key:");
        inputKeyField = new JTextField();
        JButton generateKeyButton = new JButton("Generate Key");
        generateKeyButton.addActionListener(e -> generateKey());

        JButton saveKeyButton = new JButton("Save Key to File");
        saveKeyButton.addActionListener(e -> saveKeyToFile());

        JButton loadKeyButton = new JButton("Load Key from File");
        loadKeyButton.addActionListener(e -> loadKeyFromFile());

        keyPanel.add(inputKeyLabel);
        keyPanel.add(inputKeyField);
        keyPanel.add(generateKeyButton);
        keyPanel.add(saveKeyButton);
        keyPanel.add(loadKeyButton);

        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 2, 5, 5));
        textPanel.setBorder(BorderFactory.createTitledBorder("Text Operations"));

        JLabel inputTextLabel = new JLabel("Input Text:");
        inputTextArea = new JTextArea(3, 20);
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);

        JLabel outputTextLabel = new JLabel("Output Text:");
        outputTextArea = new JTextArea(3, 20);
        outputTextArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);

        textPanel.add(inputTextLabel);
        textPanel.add(inputScrollPane);
        textPanel.add(outputTextLabel);
        textPanel.add(outputScrollPane);

        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton encryptButton = new JButton("Encrypt");
        encryptButton.addActionListener(e -> encryptText());

        JButton decryptButton = new JButton("Decrypt");
        decryptButton.addActionListener(e -> decryptText());

        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        
        add(keyPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void generateKey() {
        int keyLength = 8; 
        StringBuilder keyBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < keyLength; i++) {
            keyBuilder.append((char) ('A' + random.nextInt(26)));
        }
        inputKeyField.setText(keyBuilder.toString());
        JOptionPane.showMessageDialog(this, "Key generated successfully!");
    }

    private void saveKeyToFile() {
        String key = inputKeyField.getText().trim();
        if (key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No key to save! Please input or generate a key first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                Files.writeString(Path.of(file.getPath()), key);
                JOptionPane.showMessageDialog(this, "Key saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadKeyFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                String key = Files.readString(Path.of(file.getPath())).trim();
                inputKeyField.setText(key);
                vigenere.loadKey(key);
                JOptionPane.showMessageDialog(this, "Key loaded successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void encryptText() {
        try {
            String key = inputKeyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please input or load a key first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            vigenere.loadKey(key);

            String plaintext = inputTextArea.getText().trim();
            if (plaintext.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please input text to encrypt.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String encryptedText = vigenere.encryptText(plaintext);
            outputTextArea.setText(encryptedText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error encrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptText() {
        try {
            String key = inputKeyField.getText().trim();
            if (key.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please input or load a key first.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            vigenere.loadKey(key);

            String ciphertext = inputTextArea.getText().trim();
            if (ciphertext.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please input text to decrypt.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String decryptedText = vigenere.decryptText(ciphertext);
            outputTextArea.setText(decryptedText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error decrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Vigenère Cipher GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new VigPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
