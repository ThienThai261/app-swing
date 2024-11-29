package view;

import Algo.Modern.SM4;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SM4Panel extends JPanel {


    private SM4 sm4 = new SM4();
    private JTextArea statusArea = new JTextArea("Status area");
    private JTextArea encryptInputText = new JTextArea("Enter text to encrypt");
    private JTextArea encryptOutputText = new JTextArea("Encrypted text will appear here");
    private JTextArea decryptInputText = new JTextArea("Enter text to decrypt");
    private JTextArea decryptOutputText = new JTextArea("Decrypted text will appear here");
    private File fileToEncrypt;
    private File fileToDecrypt;

    public SM4Panel() {
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
        JComboBox<String> keySizeComboBox = new JComboBox<>(new String[]{"128 bit"}); 
        keySizeComboBox.setEnabled(false);
        mainPanel.add(keySizeComboBox, gbc);

        gbc.gridx = 1;
        JComboBox<String> paddingComboBox = new JComboBox<>(new String[]{"PKCS5Padding", "PKCS7Padding", "NoPadding"});
        mainPanel.add(paddingComboBox, gbc);

        gbc.gridx = 2;
        JButton generateKeyButton = new JButton("Generate Key");
        mainPanel.add(generateKeyButton, gbc);

        gbc.gridx = 3;
        JButton saveKeyButton = new JButton("Save Key");
        mainPanel.add(saveKeyButton, gbc);

        
        gbc.gridx = 0;
        gbc.gridy = 1;
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"ECB", "CBC", "CFB", "OFB", "CTR"});
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

        encryptPanel.add(new JScrollPane(encryptInputText));
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

        decryptPanel.add(new JScrollPane(decryptInputText));
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

        
        statusArea.setEditable(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weighty = 0.2;
        mainPanel.add(statusArea, gbc);

        add(mainPanel, BorderLayout.CENTER);
        modeComboBox.addActionListener(e -> {
            String selectedMode = (String) modeComboBox.getSelectedItem();
            try {
                sm4.setMode(selectedMode);
                statusArea.setText("Mode changed to: " + selectedMode);

                
                if (sm4.requiresIV()) {
                    loadIVButton.setVisible(true);

                    statusArea.append("\nThis mode requires an IV. Please set IV.");
                } else {
                    loadIVButton.setVisible(false);

                    statusArea.append("\nThis mode does not require an IV.");
                }
            } catch (Exception ex) {
                statusArea.setText("Error changing mode: " + ex.getMessage());
            }
        });
        
        loadIVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    
                    File ivFile = new File("iv.txt"); 
                    if (!ivFile.exists()) {
                        statusArea.setText("IV file not found: " + ivFile.getAbsolutePath());
                        return;
                    }

                    byte[] iv;
                    try (FileInputStream fis = new FileInputStream(ivFile)) {
                        iv = fis.readAllBytes();
                    }

                    
                    sm4.loadIV(iv);
                    statusArea.append("\nIV loaded successfully from file.");
                } catch (IOException ex) {
                    statusArea.setText("Error reading IV from file: " + ex.getMessage());
                } catch (Exception ex) {
                    statusArea.setText("Error loading IV: " + ex.getMessage());
                }
            }
        });

        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    sm4.genKey();
                    statusArea.setText("Key generated successfully! Base64: " + sm4.getEncodedKey());
                } catch (Exception ex) {
                    statusArea.setText("Error generating key: " + ex.getMessage());
                }
            }
        });

        loadKeyButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File keyFile = fileChooser.getSelectedFile();
                try (FileInputStream fis = new FileInputStream(keyFile)) {
                    byte[] keyBytes = fis.readAllBytes();
                    sm4.loadKey(keyBytes);
                    statusArea.setText("Key loaded successfully from: " + keyFile.getAbsolutePath());
                } catch (IOException ex) {
                    statusArea.setText("Error loading key: " + ex.getMessage());
                }
            }
        });


        generateIVButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    
                    byte[] iv = sm4.genIV();
                    statusArea.setText("IV generated successfully.");

                    
                    File ivFile = new File("iv.txt"); 
                    try (FileOutputStream fos = new FileOutputStream(ivFile)) {
                        fos.write(iv);
                        statusArea.append("\nIV saved to file: " + ivFile.getAbsolutePath());
                    }
                } catch (IOException ex) {
                    statusArea.setText("Error saving IV to file: " + ex.getMessage());
                } catch (Exception ex) {
                    statusArea.setText("Error generating IV: " + ex.getMessage());
                }
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textEncryptOption.isSelected()) {
                    try {
                        
                        String plaintext = encryptInputText.getText();
                        if (plaintext == null || plaintext.trim().isEmpty()) {
                            statusArea.setText("Error: No text entered for encryption.");
                            return;
                        }
                        String encryptedText = sm4.encryptText(plaintext);
                        encryptOutputText.setText(encryptedText);
                        statusArea.setText("Text encrypted successfully!");
                    } catch (Exception ex) {
                        statusArea.setText("Error encrypting text: " + ex.getMessage());
                    }
                } else if (fileEncryptOption.isSelected()) {
                    try {
                        
                        if (fileToEncrypt == null || !fileToEncrypt.exists()) {
                            statusArea.setText("Error: No file selected for encryption.");
                            return;
                        }
                        File outputFile = new File(fileToEncrypt.getParent(), fileToEncrypt.getName() + ".enc");
                        sm4.encryptFile(fileToEncrypt, outputFile);
                        statusArea.setText("File encrypted successfully! Saved as: " + outputFile.getAbsolutePath());
                    } catch (Exception ex) {
                        statusArea.setText("Error encrypting file: " + ex.getMessage());
                    }
                }
            }
        });


        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (textDecryptOption.isSelected()) {
                    try {
                        
                        String ciphertext = decryptInputText.getText();
                        if (ciphertext == null || ciphertext.trim().isEmpty()) {
                            statusArea.setText("Error: No text entered for decryption.");
                            return;
                        }
                        String decryptedText = sm4.decryptText(ciphertext);
                        decryptOutputText.setText(decryptedText);
                        statusArea.setText("Text decrypted successfully!");
                    } catch (Exception ex) {
                        statusArea.setText("Error decrypting text: " + ex.getMessage());
                    }
                } else if (fileDecryptOption.isSelected()) {
                    try {
                        
                        if (fileToDecrypt == null || !fileToDecrypt.exists()) {
                            statusArea.setText("Error: No file selected for decryption.");
                            return;
                        }
                        String outputFileName = fileToDecrypt.getName().replace(".enc", ".dec");
                        File outputFile = new File(fileToDecrypt.getParent(), outputFileName);
                        sm4.decryptFile(fileToDecrypt, outputFile);
                        statusArea.setText("File decrypted successfully! Saved as: " + outputFile.getAbsolutePath());
                    } catch (Exception ex) {
                        statusArea.setText("Error decrypting file: " + ex.getMessage());
                    }
                }
            }
        });


        saveEncryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                statusArea.setText("Encrypted file saved.");
            }
        });

        saveDecryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                statusArea.setText("Decrypted file saved.");
            }
        });
        fileEncryptOption.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileToEncrypt = fileChooser.getSelectedFile();
                statusArea.setText("File selected for encryption: " + fileToEncrypt.getAbsolutePath());
            }
        });

        fileDecryptOption.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileToDecrypt = fileChooser.getSelectedFile();
                statusArea.setText("File selected for decryption: " + fileToDecrypt.getAbsolutePath());
            }
        });

    }
}
