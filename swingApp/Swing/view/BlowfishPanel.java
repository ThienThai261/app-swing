package view;

import Algo.Modern.Blowfish;
import Other.CipherAlgorithm;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class BlowfishPanel extends JPanel {

    private SecretKey currentKey;
    private CipherAlgorithm cipherAlgorithm;
    private String loadedKey;
    private File fileToEncrypt;
    private File fileToDecrypt;

    public BlowfishPanel() {

        setPreferredSize(new Dimension(1600, 800));
        setLayout(new BorderLayout()); 

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JComboBox<String> paddingComboBox = new JComboBox<>(new String[]{"PKCS5Padding", "PKCS7Padding", "NoPadding"});
        mainPanel.add(paddingComboBox, gbc);

        gbc.gridx = 1;
        JButton generateKeyButton = new JButton("Generate Key");
        mainPanel.add(generateKeyButton, gbc);

        gbc.gridx = 2;
        JButton saveKeyButton = new JButton("Save Key");
        mainPanel.add(saveKeyButton, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"ECB", "CBC", "CFB", "OFB"});
        mainPanel.add(modeComboBox, gbc);

        gbc.gridx = 1;
        JButton loadKeyButton = new JButton("Load Key");
        mainPanel.add(loadKeyButton, gbc);

        gbc.gridx = 2;
        JButton loadIVButton = new JButton("Load IV");
        mainPanel.add(loadIVButton, gbc);

        gbc.gridx = 3;
        JButton generateIVButton = new JButton("Generate IV");
        mainPanel.add(generateIVButton, gbc);

        
        JPanel encryptPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        encryptPanel.setBorder(BorderFactory.createTitledBorder("Encrypt"));
        JRadioButton textEncryptOption = new JRadioButton("Text", true);
        JRadioButton fileEncryptOption = new JRadioButton("File");
        ButtonGroup encryptOptionGroup = new ButtonGroup();
        encryptOptionGroup.add(textEncryptOption);
        encryptOptionGroup.add(fileEncryptOption);

        JPanel radioEncryptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioEncryptPanel.add(textEncryptOption);
        radioEncryptPanel.add(fileEncryptOption);
        encryptPanel.add(radioEncryptPanel);

        JTextArea encryptInputText = new JTextArea("Enter text to encrypt");
        encryptPanel.add(new JScrollPane(encryptInputText));
        JTextArea encryptOutputText = new JTextArea("Encrypted text will appear here");
        encryptOutputText.setEditable(false);
        encryptPanel.add(new JScrollPane(encryptOutputText));

        JPanel encryptButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton saveEncryptButton = new JButton("Save Result File");
        JButton encryptButton = new JButton("Encrypt");
        encryptButtonPanel.add(saveEncryptButton);
        encryptButtonPanel.add(encryptButton);
        encryptPanel.add(encryptButtonPanel);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        mainPanel.add(encryptPanel, gbc);

        
        JPanel decryptPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        decryptPanel.setBorder(BorderFactory.createTitledBorder("Decrypt"));
        JRadioButton textDecryptOption = new JRadioButton("Text", true);
        JRadioButton fileDecryptOption = new JRadioButton("File");
        ButtonGroup decryptOptionGroup = new ButtonGroup();
        decryptOptionGroup.add(textDecryptOption);
        decryptOptionGroup.add(fileDecryptOption);

        JPanel radioDecryptPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioDecryptPanel.add(textDecryptOption);
        radioDecryptPanel.add(fileDecryptOption);
        decryptPanel.add(radioDecryptPanel);

        JTextArea decryptInputText = new JTextArea("Enter text to decrypt");
        decryptPanel.add(new JScrollPane(decryptInputText));
        JTextArea decryptOutputText = new JTextArea("Decrypted text will appear here");
        decryptOutputText.setEditable(false);
        decryptPanel.add(new JScrollPane(decryptOutputText));

        JPanel decryptButtonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        JButton saveDecryptButton = new JButton("Save Result File");
        JButton decryptButton = new JButton("Decrypt");
        decryptButtonPanel.add(saveDecryptButton);
        decryptButtonPanel.add(decryptButton);
        decryptPanel.add(decryptButtonPanel);

        gbc.gridx = 2;
        gbc.gridwidth = 2;
        mainPanel.add(decryptPanel, gbc);

        
        JTextArea statusArea = new JTextArea("Status area");
        statusArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weighty = 0.2;
        mainPanel.add(statusArea, gbc);

        add(mainPanel, BorderLayout.CENTER);

        
        generateKeyButton.addActionListener(e -> {
            try {
                if (cipherAlgorithm == null) {
                    cipherAlgorithm = new Blowfish(); 
                }

                
                int keySize = Integer.parseInt(JOptionPane.showInputDialog(
                        this,
                        "Enter key size (32-448 bits, multiple of 8):",
                        "128"
                ));

                
                if (keySize < 32 || keySize > 448 || keySize % 8 != 0) {
                    throw new IllegalArgumentException("Key size must be between 32 and 448 bits, and a multiple of 8.");
                }

                
                SecretKey generatedKey = cipherAlgorithm.genKey(keySize);
                loadedKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());

                
                statusArea.setText("Key generated successfully.");
            } catch (Exception ex) {
                statusArea.setText("Error generating key: " + ex.getMessage());
            }
        });

        saveKeyButton.addActionListener(e -> {
            try {
                if (loadedKey == null) {
                    statusArea.setText("Please generate a key before saving.");
                    return;
                }
                JFileChooser fileChooser = new JFileChooser();
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    Files.write(fileToSave.toPath(), loadedKey.getBytes(StandardCharsets.UTF_8));
                    statusArea.setText("Key saved successfully.");
                }
            } catch (IOException ex) {
                statusArea.setText("Error saving key: " + ex.getMessage());
            }
        });

        loadKeyButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    byte[] keyData = Files.readAllBytes(file.toPath());
                    loadedKey = new String(keyData, StandardCharsets.UTF_8);
                    cipherAlgorithm.loadKey(Base64.getDecoder().decode(loadedKey));
                    statusArea.setText("Key loaded successfully.");
                }
            } catch (IOException ex) {
                statusArea.setText("Error loading key: " + ex.getMessage());
            }
        });
        generateIVButton.addActionListener(e -> {
            byte[] iv = cipherAlgorithm.genIV();
            statusArea.setText("IV generated successfully: " + Base64.getEncoder().encodeToString(iv));
        });
        loadIVButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    byte[] ivData = Files.readAllBytes(file.toPath());
                    if (ivData.length != 8) {
                        throw new IllegalArgumentException("IV must be exactly 8 bytes for Blowfish.");
                    }
                    cipherAlgorithm.setIV(ivData);
                    statusArea.setText("IV loaded successfully.");
                }
            } catch (Exception ex) {
                statusArea.setText("Error loading IV: " + ex.getMessage());
            }
        });
        encryptButton.addActionListener(e -> {
            try {
                if (textEncryptOption.isSelected()) {
                    
                    String inputText = encryptInputText.getText();
                    if (inputText.isEmpty()) {
                        statusArea.setText("Please enter text to encrypt.");
                        return;
                    }
                    String mode = (String) modeComboBox.getSelectedItem();
                    String padding = (String) paddingComboBox.getSelectedItem();
                    String encryptedText = cipherAlgorithm.encrypt(inputText, loadedKey, padding, mode);
                    encryptOutputText.setText(encryptedText);
                    statusArea.setText("Text encrypted successfully.");
                } else if (fileEncryptOption.isSelected()) {
                    
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        fileToEncrypt = fileChooser.getSelectedFile();
                        String mode = (String) modeComboBox.getSelectedItem();
                        String padding = (String) paddingComboBox.getSelectedItem();
                        cipherAlgorithm.encryptFile(fileToEncrypt, loadedKey, padding, mode);
                        statusArea.setText("File encrypted successfully.");
                    } else {
                        statusArea.setText("No file selected for encryption.");
                    }
                }
            } catch (Exception ex) {
                statusArea.setText("Error during encryption: " + ex.getMessage());
            }
        });

        decryptButton.addActionListener(e -> {
            try {
                if (textDecryptOption.isSelected()) {
                    
                    String encryptedText = encryptOutputText.getText();
                    if (encryptedText.isEmpty()) {
                        statusArea.setText("Please enter text to decrypt.");
                        return;
                    }
                    String mode = (String) modeComboBox.getSelectedItem();
                    String padding = (String) paddingComboBox.getSelectedItem();
                    String decryptedText = cipherAlgorithm.decrypt(encryptedText, loadedKey, padding, mode);
                    decryptOutputText.setText(decryptedText);
                    statusArea.setText("Text decrypted successfully.");
                } else if (fileDecryptOption.isSelected()) {
                    
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(this);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        fileToDecrypt = fileChooser.getSelectedFile();
                        String mode = (String) modeComboBox.getSelectedItem();
                        String padding = (String) paddingComboBox.getSelectedItem();
                        System.out.println("Selected file to decrypt: " + fileToDecrypt.getAbsolutePath());
                        System.out.println("Mode: " + mode + ", Padding: " + padding);
                        cipherAlgorithm.decryptFile(fileToDecrypt, loadedKey, padding, mode);
                        statusArea.setText("File decrypted successfully.");
                    } else {
                        statusArea.setText("No file selected for decryption.");
                    }
                }
            } catch (Exception ex) {
                statusArea.setText("Error during decryption: " + ex.getMessage());
                ex.printStackTrace(); 
            }
        });


    }
}
