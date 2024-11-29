package view;

import Algo.Modern.AES;
import Other.CipherAlgorithm;

import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

public class AESPanel extends JPanel {

    private SecretKey currentKey;
    private CipherAlgorithm cipherAlgorithm;
    private String loadedKey;
    private File fileToEncrypt;
    private File fileToDecrypt;
    private JPanel contentPanel;

    public AESPanel
            () {

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
        JComboBox<String> keySizeComboBox = new JComboBox<>(new String[]{"128 bit", "192 bit", "256 bit"});
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
        JComboBox<String> modeComboBox = new JComboBox<>(new String[]{"ECB", "CBC", "PCBC", "CFB", "OFB", "CTR"});
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

        JTextArea statusArea = new JTextArea("Status place");
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

        generateKeyButton.addActionListener(e -> {
            try {
                cipherAlgorithm = (CipherAlgorithm) new AES();
                String selectedKeySize = (String) keySizeComboBox.getSelectedItem();
                int keySize = Integer.parseInt(selectedKeySize.split(" ")[0]);
                SecretKey generatedKey = cipherAlgorithm.genKey(keySize);
                loadedKey = Base64.getEncoder().encodeToString(generatedKey.getEncoded());
                statusArea.setText("Key generated successfully and ready to be saved.");
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
                    statusArea.setText("Key saved successfully to " + fileToSave.getAbsolutePath());
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
                    statusArea.setText("Key loaded successfully from " + file.getAbsolutePath());
                }
            } catch (IOException ex) {
                statusArea.setText("Error loading key: " + ex.getMessage());
            }
        });

        modeComboBox.addActionListener(e -> {
            if (cipherAlgorithm instanceof AES) {
                ((AES) cipherAlgorithm).setMode((String) modeComboBox.getSelectedItem());
            }
        });

        paddingComboBox.addActionListener(e -> {
            if (cipherAlgorithm instanceof AES) {
                String selectedPadding = (String) paddingComboBox.getSelectedItem();
                
                if ("No Padding".equals(selectedPadding)) {
                    ((AES) cipherAlgorithm).setPadding("NoPadding");
                } else if ("Pkcs5".equals(selectedPadding)) {
                    ((AES) cipherAlgorithm).setPadding("PKCS5Padding");
                } else if ("Pkcs7".equals(selectedPadding)) {
                    ((AES) cipherAlgorithm).setPadding("PKCS7Padding");
                }
            }
        });

        encryptButton.addActionListener(e -> {
            try {
                if (loadedKey == null) {
                    statusArea.setText("Please load a key before encryption.");
                    return;
                }

                String mode = (String) modeComboBox.getSelectedItem();
                String padding = (String) paddingComboBox.getSelectedItem();

                if (textEncryptOption.isSelected()) {
                    String inputText = encryptInputText.getText();
                    cipherAlgorithm = new AES();
                    String encryptedText = cipherAlgorithm.encrypt(inputText, loadedKey, padding, mode);
                    encryptOutputText.setText(encryptedText);
                    statusArea.setText("Text encrypted successfully!");

                } else if (fileEncryptOption.isSelected() && fileToEncrypt != null) {
                    
                    cipherAlgorithm = new AES();
                    cipherAlgorithm.encryptFile(fileToEncrypt, loadedKey, padding, mode);
                    statusArea.setText("File encrypted successfully: " + fileToEncrypt.getAbsolutePath());
                }


            } catch (Exception ex) {
                statusArea.setText("Error during encryption: " + ex.getMessage());
            }
        });


        decryptButton.addActionListener(e -> {
            try {
                if (loadedKey == null) {
                    statusArea.setText("Please load a key before decryption.");
                    return;
                }
                byte[] iv = Base64.getDecoder().decode("base64EncodedIV"); 
                ((AES) cipherAlgorithm).setIV(iv);

                String mode = (String) modeComboBox.getSelectedItem();
                String padding = (String) paddingComboBox.getSelectedItem();

                
                if (textDecryptOption.isSelected()) {
                    String inputText = decryptInputText.getText();
                    String decryptedText = cipherAlgorithm.decrypt(inputText, loadedKey, padding, mode);
                    decryptOutputText.setText(decryptedText);
                    statusArea.setText("Text decrypted successfully!");
                }
                else if (fileDecryptOption.isSelected() && fileToDecrypt != null) {
                    
                    cipherAlgorithm = new AES();
                    cipherAlgorithm.decryptFile(fileToDecrypt, loadedKey, padding, mode);
                    statusArea.setText("File encrypted successfully: " + fileToDecrypt.getAbsolutePath());
                }
            } catch (Exception ex) {
                statusArea.setText("Error during encryption: " + ex.getMessage());
            }
        });
        generateIVButton.addActionListener(e -> {
            try {
                String mode = (String) modeComboBox.getSelectedItem();

                
                if (!mode.equals("ECB")) {
                    cipherAlgorithm = new AES(); 

                    
                    byte[] iv = ((AES) cipherAlgorithm).genIV();
                    String encodedIV = Base64.getEncoder().encodeToString(iv);

                    statusArea.setText("IV generated successfully for mode " + mode + ": " + encodedIV);

                    
                    int saveOption = JOptionPane.showConfirmDialog(this, "Would you like to save this IV?",
                            "Save IV", JOptionPane.YES_NO_OPTION);

                    if (saveOption == JOptionPane.YES_OPTION) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setDialogTitle("Save IV");
                        int userSelection = fileChooser.showSaveDialog(this);
                        if (userSelection == JFileChooser.APPROVE_OPTION) {
                            File fileToSave = fileChooser.getSelectedFile();
                            Files.write(fileToSave.toPath(), encodedIV.getBytes(StandardCharsets.UTF_8));
                            statusArea.setText("IV saved successfully to " + fileToSave.getAbsolutePath());
                        }
                    }
                } else {
                    statusArea.setText("IV is not needed for ECB mode.");
                }
            } catch (Exception ex) {
                statusArea.setText("Error generating IV: " + ex.getMessage());
            }
        });

        loadIVButton.addActionListener(e -> {
            try {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Load IV");
                int returnValue = fileChooser.showOpenDialog(this);

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    byte[] ivData = Files.readAllBytes(file.toPath());
                    String encodedIV = new String(ivData, StandardCharsets.UTF_8);

                    
                    byte[] iv = Base64.getDecoder().decode(encodedIV);
                    ((AES) cipherAlgorithm).setIV(iv); 

                    statusArea.setText("IV loaded successfully from " + file.getAbsolutePath());
                }
            } catch (IOException ex) {
                statusArea.setText("Error loading IV: " + ex.getMessage());
            }
        });

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RSA Encryption/Decryption");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new AESPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}


