package view;

import Algo.Classic.CaesarCipher;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class CaesarPanel extends JPanel {
    private JTextArea textInputArea;
    private JTextArea outputArea;
    private JTextArea statusArea;
    private File selectedFile;
    private int shiftKey;

    public CaesarPanel() {
        setLayout(new BorderLayout());

        
        JPanel keyPanel = new JPanel(new FlowLayout());
        keyPanel.setBorder(BorderFactory.createTitledBorder("Key Operations"));

        JButton generateKeyButton = new JButton("Generate Key");
        generateKeyButton.addActionListener(e -> showKeyDialog());
        keyPanel.add(generateKeyButton);

        add(keyPanel, BorderLayout.NORTH);

        
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Text Operations"));
        textInputArea = new JTextArea("Enter text here...");
        textPanel.add(new JScrollPane(textInputArea), BorderLayout.CENTER);

        JPanel textButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton encryptTextButton = new JButton("Encrypt Text");
        encryptTextButton.addActionListener(e -> encryptText());
        JButton decryptTextButton = new JButton("Decrypt Text");
        decryptTextButton.addActionListener(e -> decryptText());

        textButtonPanel.add(encryptTextButton);
        textButtonPanel.add(decryptTextButton);
        textPanel.add(textButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(textPanel);

        
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(BorderFactory.createTitledBorder("File Operations"));

        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(e -> chooseFile());
        filePanel.add(chooseFileButton, BorderLayout.NORTH);

        JPanel fileButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton encryptFileButton = new JButton("Encrypt File");
        encryptFileButton.addActionListener(e -> encryptFile());
        JButton decryptFileButton = new JButton("Decrypt File");
        decryptFileButton.addActionListener(e -> decryptFile());

        fileButtonPanel.add(encryptFileButton);
        fileButtonPanel.add(decryptFileButton);
        filePanel.add(fileButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(filePanel);

        add(mainPanel, BorderLayout.CENTER);

        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Output and Status"));

        outputArea = new JTextArea("Output will appear here...");
        outputArea.setEditable(false);
        bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        statusArea = new JTextArea("Status will appear here...");
        statusArea.setEditable(false);
        bottomPanel.add(new JScrollPane(statusArea), BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showKeyDialog() {
        JDialog keyDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Generate or Enter Key", true);
        keyDialog.setLayout(new GridLayout(3, 1, 10, 10));
        keyDialog.setSize(300, 200);

        
        JButton randomKeyButton = new JButton("Randomly Generate Key");
        randomKeyButton.addActionListener(e -> {
            shiftKey = (int) (Math.random() * 25) + 1;
            statusArea.setText("Key generated: " + shiftKey);
            keyDialog.dispose();
        });

        
        JPanel manualKeyPanel = new JPanel(new BorderLayout());
        JTextField manualKeyField = new JTextField();
        manualKeyPanel.add(new JLabel("Enter Key (1-25):"), BorderLayout.NORTH);
        manualKeyPanel.add(manualKeyField, BorderLayout.CENTER);

        JButton confirmKeyButton = new JButton("Confirm Key");
        confirmKeyButton.addActionListener(e -> {
            try {
                int enteredKey = Integer.parseInt(manualKeyField.getText().trim());
                if (enteredKey < 1 || enteredKey > 25) {
                    throw new NumberFormatException("Key must be between 1 and 25.");
                }
                shiftKey = enteredKey;
                statusArea.setText("Key set to: " + shiftKey);
                keyDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(keyDialog, "Invalid key. Please enter a number between 1 and 25.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        keyDialog.add(randomKeyButton);
        keyDialog.add(manualKeyPanel);
        keyDialog.add(confirmKeyButton);

        keyDialog.setVisible(true);
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            statusArea.setText("Selected file: " + selectedFile.getAbsolutePath());
        }
    }

    private void encryptText() {
        try {
            String inputText = textInputArea.getText();
            if (inputText.isEmpty()) {
                statusArea.setText("Please enter text to encrypt.");
                return;
            }
            String encryptedText = CaesarCipher.encryptText(inputText, shiftKey);
            outputArea.setText(encryptedText);
            statusArea.setText("Text encrypted successfully.");
        } catch (Exception ex) {
            statusArea.setText("Error encrypting text: " + ex.getMessage());
        }
    }

    private void decryptText() {
        try {
            String inputText = textInputArea.getText();
            if (inputText.isEmpty()) {
                statusArea.setText("Please enter text to decrypt.");
                return;
            }
            String decryptedText = CaesarCipher.decryptText(inputText, shiftKey);
            outputArea.setText(decryptedText);
            statusArea.setText("Text decrypted successfully.");
        } catch (Exception ex) {
            statusArea.setText("Error decrypting text: " + ex.getMessage());
        }
    }

    private void encryptFile() {
        try {
            if (selectedFile == null) {
                statusArea.setText("Please select a file to encrypt.");
                return;
            }
            String content = Files.readString(selectedFile.toPath());
            String encryptedContent = CaesarCipher.encryptText(content, shiftKey);
            Files.writeString(selectedFile.toPath(), encryptedContent);
            statusArea.setText("File encrypted successfully.");
        } catch (IOException ex) {
            statusArea.setText("Error encrypting file: " + ex.getMessage());
        }
    }

    private void decryptFile() {
        try {
            if (selectedFile == null) {
                statusArea.setText("Please select a file to decrypt.");
                return;
            }
            String content = Files.readString(selectedFile.toPath());
            String decryptedContent = CaesarCipher.decryptText(content, shiftKey);
            Files.writeString(selectedFile.toPath(), decryptedContent);
            statusArea.setText("File decrypted successfully.");
        } catch (IOException ex) {
            statusArea.setText("Error decrypting file: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Caesar Cipher Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new CaesarPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
