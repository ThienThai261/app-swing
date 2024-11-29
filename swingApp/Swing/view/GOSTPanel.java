package view;

import Algo.Modern.GOST;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class GOSTPanel extends JPanel {

    private byte[] generatedIV;
    private SecretKey currentKey;
    private GOST gost = new GOST();
    private String loadedKey;
    private File fileToEncrypt;
    private File fileToDecrypt;

    private String selectedPadding = "NoPadding"; 
    private String selectedMode = "ECB";

    public GOSTPanel() {
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
        JComboBox<String> keySizeComboBox = new JComboBox<>(new String[]{"256 bit"}); 
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

        JScrollPane scrollPane = new JScrollPane(mainPanel); 
        add(scrollPane, BorderLayout.CENTER);
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

        
        generateKeyButton.addActionListener(e -> generateKey(statusArea));
        saveKeyButton.addActionListener(e -> saveKey(statusArea));
        generateIVButton.addActionListener(e -> generateIV(statusArea));
        loadIVButton.addActionListener(e -> loadIV(statusArea));
        loadKeyButton.addActionListener(e -> loadKey(statusArea));
        modeComboBox.addActionListener(e -> {
            
            String selectedMode = modeComboBox.getSelectedItem().toString();
            String selectedPadding = paddingComboBox.getSelectedItem().toString();
            
            statusArea.setText("Mode: " + selectedMode + " | Padding: " + selectedPadding);
        });
        encryptButton.addActionListener(e -> {
            
            selectedMode = (String) modeComboBox.getSelectedItem();
            selectedPadding = (String) paddingComboBox.getSelectedItem();
            statusArea.setText("Mode change to"+selectedMode + " "+ "Padding change to"+ selectedPadding);
            
            encryptText(statusArea, encryptInputText, encryptOutputText, textEncryptOption);
        });

        decryptButton.addActionListener(e -> decryptText(statusArea, decryptInputText, decryptOutputText, textDecryptOption));
        encryptButton.addActionListener(e -> {
            if (textEncryptOption.isSelected()) {
                encryptText(statusArea, encryptInputText, encryptOutputText, textEncryptOption);
            }
            else if(fileEncryptOption.isSelected()){
                try {
                    encryptFile(statusArea);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        modeComboBox.addActionListener(e -> {

               gost.setMode((String) modeComboBox.getSelectedItem());
            })
        ;

        decryptButton.addActionListener(e -> {
            if (textDecryptOption.isSelected()) {
                decryptText(statusArea, encryptInputText, encryptOutputText, textEncryptOption);
            }
            else if(fileDecryptOption.isSelected()){
                try {
                    decryptFile(statusArea);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }
    private void generateKey(JTextArea statusArea) {
        try {

            currentKey = gost.genKeyGost();
            String keyBase64 = Base64.getEncoder().encodeToString(currentKey.getEncoded());
            statusArea.setText("Key generated successfully."+keyBase64);
        } catch (Exception e) {
            statusArea.setText("Failed to generate key: " + e.getMessage());
        }
    }

    private void saveKey(JTextArea statusArea) {
        if (currentKey == null) {
            statusArea.setText("No key available to save. Generate a key first.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showSaveDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File keyFile = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(keyFile)) {
                
                String encodedKey = Base64.getEncoder().encodeToString(currentKey.getEncoded());
                writer.write(encodedKey);
                statusArea.setText("Key saved successfully to file: " + keyFile.getAbsolutePath());
            } catch (IOException e) {
                statusArea.setText("Failed to save key: " + e.getMessage());
            }
        }
    }


    public void loadKey(JTextArea statusArea) {
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File keyFile = fileChooser.getSelectedFile();
            try {
                String encodedKey = new String(Files.readAllBytes(keyFile.toPath()));
                byte[] keyData = Base64.getDecoder().decode(encodedKey);
                if (keyData.length != 32) {
                    statusArea.setText("Key length is invalid. It must be 32 bytes.");
                    return;
                }
                currentKey = new SecretKeySpec(keyData, "GOST28147");
                loadedKey = encodedKey; 
                statusArea.setText("Key loaded successfully from file: " + keyFile.getAbsolutePath());
            } catch (IOException e) {
                statusArea.setText("Failed to load key: " + e.getMessage());
            }
        }
    }



    private void encryptText(JTextArea statusArea, JTextArea inputText, JTextArea outputText, JRadioButton textOption) {
        try {
            
            if (currentKey == null) {
                statusArea.setText("No key available for encryption. Generate or load a key first.");
                return;
            }
            
            byte[] keyBytes = currentKey.getEncoded();
            int keyLength = keyBytes.length;
            if (keyLength != 32) {
                statusArea.setText("Key length is invalid. It must be 32 bytes.");
                return;
            }

            
            if (textOption.isSelected()) {

                String plainText = inputText.getText();
                String encryptedText = gost.encryptText(plainText, currentKey, selectedMode, selectedPadding);
                outputText.setText(encryptedText);
                statusArea.setText("Text encrypted successfully.");
            }
        } catch (Exception e) {
            statusArea.setText("Encryption failed: " + e.getMessage());
        }
    }

    private void encryptFile(JTextArea statusArea) throws Exception {

                
                String outputFile = fileToEncrypt.getAbsolutePath() + ".enc";

                
                gost.encryptFile(fileToEncrypt.getAbsolutePath(), outputFile, currentKey, selectedMode, selectedPadding);
                
                statusArea.setText("File encrypted successfully: " + outputFile);
        }



    private void decryptText(JTextArea statusArea, JTextArea inputText, JTextArea outputText, JRadioButton textOption) {
        try {
            
            if (currentKey == null) {
                statusArea.setText("No key available for decryption. Generate or load a key first.");
                return;
            }

            
            byte[] keyBytes = currentKey.getEncoded();
            int keyLength = keyBytes.length;
            System.out.println("Key Length: " + keyLength + " bytes"); 

            
            if (keyLength != 32) {
                statusArea.setText("Key length is invalid. It must be 32 bytes.");
                return;
            }

            
            if (textOption.isSelected()) {
                String encryptedText = inputText.getText();

                
                String decryptedText = gost.decryptText(encryptedText, currentKey, selectedMode, selectedPadding);
                outputText.setText(decryptedText);
                statusArea.setText("Text decrypted successfully.");
            }
        } catch (Exception e) {
            statusArea.setText("Decryption failed: " + e.getMessage());
        }
    }




    private void decryptFile(JTextArea statusArea) throws Exception {

        
        String outputFile = fileToDecrypt.getAbsolutePath() + ".des";

        
        gost.decryptFile(fileToDecrypt.getAbsolutePath(), outputFile, currentKey, selectedMode, selectedPadding);
        
        statusArea.setText("File encrypted successfully: " + outputFile);
    }



    private void generateIV(JTextArea statusArea) {
        try {
            
            generatedIV = gost.genIV();
            statusArea.setText("IV generated successfully.");

            
            int response = JOptionPane.showConfirmDialog(
                    this,
                    "Do you want to save the generated IV?",
                    "Save IV",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                
                JFileChooser fileChooser = new JFileChooser();
                int fileChoice = fileChooser.showSaveDialog(this);
                if (fileChoice == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    
                    try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                        fos.write(generatedIV);
                        statusArea.setText("IV saved successfully to: " + fileToSave.getAbsolutePath());
                    } catch (IOException e) {
                        statusArea.setText("Failed to save IV: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            statusArea.setText("Failed to generate IV: " + e.getMessage());
        }
    }


    private void loadIV(JTextArea statusArea) {
        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(this);
        if (choice == JFileChooser.APPROVE_OPTION) {
            File ivFile = fileChooser.getSelectedFile();
            try {
                generatedIV = Files.readAllBytes(ivFile.toPath());
                gost.setIV(generatedIV);
                statusArea.setText("IV loaded successfully from file: " + ivFile.getAbsolutePath());
            } catch (IOException e) {
                statusArea.setText("Failed to load IV: " + e.getMessage());
            }
        }
    }


}
