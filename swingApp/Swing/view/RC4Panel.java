package view;

import Algo.Modern.RC4;
import Other.CipherAlgorithm;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class RC4Panel extends JPanel {

    private SecretKey currentKey;
    private CipherAlgorithm cipherAlgorithm;
    private String loadedKey;
    private File fileToEncrypt;
    private File fileToDecrypt;
    private JPanel contentPanel;

    public RC4Panel() {
        this.setPreferredSize(new Dimension(15000, 200000));  
        revalidate();
        repaint();
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        
        gbc.gridx = 0;
        gbc.gridy = 0;
        JTextField keySizeInputField = new JTextField("128");  
        mainPanel.add(keySizeInputField, gbc);

        gbc.gridx = 1;
        JComboBox<String> paddingComboBox = new JComboBox<>(new String[]{"PKCS5Padding", "PKCS7Padding", "NoPadding"});
        paddingComboBox.setEnabled(false);  
        mainPanel.add(paddingComboBox, gbc);

        gbc.gridx = 2;
        JButton generateKeyButton = new JButton("Generate Key");
        mainPanel.add(generateKeyButton, gbc);

        gbc.gridx = 3;
        JButton saveKeyButton = new JButton("Save Key");
        mainPanel.add(saveKeyButton, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"ECB", "CBC", "PCBC", "CFB", "OFB", "CTR"});
        modeComboBox.setEnabled(false);  
        mainPanel.add(modeComboBox, gbc);

        gbc.gridx = 1;
        JButton loadKeyButton = new JButton("Load Key");
        mainPanel.add(loadKeyButton, gbc);

        gbc.gridx = 2;
        JButton loadIVButton = new JButton("Load IV");
        loadIVButton.setEnabled(false);  
        mainPanel.add(loadIVButton, gbc);

        gbc.gridx = 3;
        JButton generateIVButton = new JButton("Generate IV");
        generateIVButton.setEnabled(false);  
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

        
        JTextArea statusArea = new JTextArea("Status place");
        statusArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weighty = 0.2;
        mainPanel.add(statusArea, gbc);

        
        JPanel cardPanel = new JPanel(new CardLayout());
        add(cardPanel, BorderLayout.CENTER);  
        add(mainPanel, BorderLayout.CENTER);
        setSize(2000, 100);
        loadKeyButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    byte[] keyData = Files.readAllBytes(file.toPath());
                    loadedKey = new String(keyData, StandardCharsets.UTF_8);
                    statusArea.setText("Key loaded successfully from " + file.getAbsolutePath());
                }
            } catch (IOException ex) {
                statusArea.setText("Error loading key: " + ex.getMessage());
            }
        });
        
        fileEncryptOption.addActionListener(e -> {
            if (fileEncryptOption.isSelected()) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileToEncrypt = fileChooser.getSelectedFile();
                    statusArea.setText("File selected for encryption: " + fileToEncrypt.getAbsolutePath());
                }
            }
        });

        fileDecryptOption.addActionListener(e -> {
            if (fileDecryptOption.isSelected()) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileToDecrypt = fileChooser.getSelectedFile();
                    statusArea.setText("File selected for decryption: " + fileToDecrypt.getAbsolutePath());
                }
            }
        });

        generateKeyButton.addActionListener(e -> {
            try {
                String keySizeText = keySizeInputField.getText();
                int keySize = Integer.parseInt(keySizeText);

                if (keySize < 40 || keySize > 2048) {
                    statusArea.setText("Invalid key size. Please enter a value between 40 and 2048.");
                    return;
                }

                cipherAlgorithm = new RC4();
                SecretKey generatedKey = cipherAlgorithm.genKey(keySize);  
                loadedKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());
                statusArea.setText("Key generated successfully and ready to be saved.");
            } catch (NumberFormatException ex) {
                statusArea.setText("Invalid input for key size.");
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
                fileChooser.setDialogTitle("Save Key");
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    Files.write(fileToSave.toPath(), loadedKey.getBytes(StandardCharsets.UTF_8));
                    statusArea.setText("Key saved to " + fileToSave.getAbsolutePath());
                }
            } catch (IOException ex) {
                statusArea.setText("Error saving key: " + ex.getMessage());
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
                    cipherAlgorithm = new RC4();
                    String encryptedText = cipherAlgorithm.encrypt(inputText, loadedKey,null,null);
                    encryptOutputText.setText(encryptedText);
                } else if (fileEncryptOption.isSelected()) {
                    if (fileToEncrypt == null) {
                        statusArea.setText("Please select a file to encrypt.");
                        return;
                    }
                    String fileContent = new String(Files.readAllBytes(fileToEncrypt.toPath()));
                    cipherAlgorithm = new RC4();
                    String encryptedContent = cipherAlgorithm.encrypt(fileContent, loadedKey,null,null);
                    Files.write(fileToEncrypt.toPath(), encryptedContent.getBytes());
                    statusArea.setText("File encrypted successfully.");
                }
            } catch (IOException ex) {
                statusArea.setText("Error encrypting: " + ex.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        decryptButton.addActionListener(e -> {
            try {
                if (textDecryptOption.isSelected()) {
                    String inputText = decryptInputText.getText();
                    if (inputText.isEmpty()) {
                        statusArea.setText("Please enter text to decrypt.");
                        return;
                    }
                    cipherAlgorithm = new RC4();
                    String decryptedText = cipherAlgorithm.decrypt(inputText, loadedKey,null,null);
                    decryptOutputText.setText(decryptedText);
                } else if (fileDecryptOption.isSelected()) {
                    if (fileToDecrypt == null) {
                        statusArea.setText("Please select a file to decrypt.");
                        return;
                    }
                    String fileContent = new String(Files.readAllBytes(fileToDecrypt.toPath()));
                    cipherAlgorithm = new RC4();
                    String decryptedContent = cipherAlgorithm.decrypt(fileContent, loadedKey,null,null);
                    Files.write(fileToDecrypt.toPath(), decryptedContent.getBytes());
                    statusArea.setText("File decrypted successfully.");
                }
            } catch (IOException ex) {
                statusArea.setText("Error decrypting: " + ex.getMessage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
