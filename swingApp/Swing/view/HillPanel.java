package view;

import Algo.Classic.Hill;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class HillPanel extends JPanel {

    private final Hill hill;
    private JComboBox<Integer> keySizeComboBox;
    private JTextField inputKeyField;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;

    public HillPanel() {
        this.hill = new Hill();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new GridLayout(4, 2, 5, 5)); 
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key Operations"));

        JLabel keySizeLabel = new JLabel("Key Size:");
        keySizeComboBox = new JComboBox<>(new Integer[]{2, 3}); 

        JLabel inputKeyLabel = new JLabel("Key:");
        inputKeyField = new JTextField();
        inputKeyField.setEditable(false);

        JButton generateKeyButton = new JButton("Generate Key");
        generateKeyButton.addActionListener(e -> generateKey());

        JButton saveKeyButton = new JButton("Save Key to File");
        saveKeyButton.addActionListener(e -> saveKeyToFile());

        JButton loadKeyButton = new JButton("Load Key from File");
        loadKeyButton.addActionListener(e -> loadKeyFromFile());

        keyPanel.add(keySizeLabel);
        keyPanel.add(keySizeComboBox);
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
        try {
            int size = (Integer) keySizeComboBox.getSelectedItem();
            int[][] key = hill.genKey(size);
            StringBuilder keyBuilder = new StringBuilder();
            for (int[] row : key) {
                for (int val : row) {
                    keyBuilder.append(val).append(" ");
                }
            }
            inputKeyField.setText(keyBuilder.toString().trim());
            JOptionPane.showMessageDialog(this, "Key generated successfully!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveKeyToFile() {
        String keyString = inputKeyField.getText().trim();
        if (keyString.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No key to save! Generate a key first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(keyString);
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
            try (Scanner scanner = new Scanner(file)) {
                StringBuilder keyBuilder = new StringBuilder();
                while (scanner.hasNextLine()) {
                    keyBuilder.append(scanner.nextLine()).append(" ");
                }
                String keyString = keyBuilder.toString().trim();
                int size = (Integer) keySizeComboBox.getSelectedItem();
                hill.loadKey(keyString, size); 
                inputKeyField.setText(keyString);
                JOptionPane.showMessageDialog(this, "Key loaded successfully!");
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "File not found: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid key in file: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void encryptText() {
        try {
            String inputText = inputTextArea.getText().trim();
            int size = (Integer) keySizeComboBox.getSelectedItem();
            int[][] key = hill.loadKey(inputKeyField.getText().trim(), size);
            String encryptedText = hill.encryptText(inputText, key);
            outputTextArea.setText(encryptedText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error encrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void decryptText() {
        try {
            String encryptedText = inputTextArea.getText().trim();
            int size = (Integer) keySizeComboBox.getSelectedItem();
            int[][] key = hill.loadKey(inputKeyField.getText().trim(), size);
            String decryptedText = hill.decryptText(encryptedText, key);
            outputTextArea.setText(decryptedText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error decrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Hill Cipher GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new HillPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
