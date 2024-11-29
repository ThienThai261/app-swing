package view;

import Algo.Modern.RSA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RSAPanel extends JPanel {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JTextArea publicKeyTextArea;
    private JTextArea privateKeyTextArea;
    private JButton generateKeyButton;
    private JButton encryptButton;
    private JButton decryptButton;

    private RSA rsa; 

    public RSAPanel() {
        setLayout(new BorderLayout());

        
        JPanel keyPanel = new JPanel(new GridLayout(2, 1, 10, 10)); 
        JPanel inputOutputPanel = new JPanel(new GridLayout(2, 1, 10, 10)); 
        JPanel buttonPanel = new JPanel(new FlowLayout()); 

        
        publicKeyTextArea = new JTextArea(5, 50);
        privateKeyTextArea = new JTextArea(5, 50);
        publicKeyTextArea.setBorder(BorderFactory.createTitledBorder("Public Key"));
        privateKeyTextArea.setBorder(BorderFactory.createTitledBorder("Private Key"));
        publicKeyTextArea.setLineWrap(true);
        privateKeyTextArea.setLineWrap(true);
        publicKeyTextArea.setWrapStyleWord(true);
        privateKeyTextArea.setWrapStyleWord(true);

        keyPanel.add(new JScrollPane(publicKeyTextArea));
        keyPanel.add(new JScrollPane(privateKeyTextArea));

        
        inputTextArea = new JTextArea(5, 50);
        outputTextArea = new JTextArea(5, 50);
        inputTextArea.setBorder(BorderFactory.createTitledBorder("Input Text"));
        outputTextArea.setBorder(BorderFactory.createTitledBorder("Output Text"));
        inputTextArea.setLineWrap(true);
        outputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        outputTextArea.setWrapStyleWord(true);

        inputOutputPanel.add(new JScrollPane(inputTextArea));
        inputOutputPanel.add(new JScrollPane(outputTextArea));

        
        generateKeyButton = new JButton("Generate Keys");
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        buttonPanel.add(generateKeyButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        
        add(keyPanel, BorderLayout.NORTH);
        add(inputOutputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        
        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    rsa = new RSA(); 
                    publicKeyTextArea.setText(rsa.getPublicKeyBase64());
                    privateKeyTextArea.setText(rsa.getPrivateKeyBase64());
                    JOptionPane.showMessageDialog(RSAPanel.this, "Key pair generated successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RSAPanel.this, "Error generating keys: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (rsa == null || rsa.getPublicKeyBase64() == null) {
                        JOptionPane.showMessageDialog(RSAPanel.this, "Please generate or provide a public key first!", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String inputText = inputTextArea.getText();
                    if (inputText.isEmpty()) {
                        JOptionPane.showMessageDialog(RSAPanel.this, "Input text is empty!", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String encryptedText = rsa.encrypt(inputText);
                    outputTextArea.setText(encryptedText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RSAPanel.this, "Error encrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (rsa == null || rsa.getPrivateKeyBase64() == null) {
                        JOptionPane.showMessageDialog(RSAPanel.this, "Please generate or provide a private key first!", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String encryptedText = inputTextArea.getText();
                    if (encryptedText.isEmpty()) {
                        JOptionPane.showMessageDialog(RSAPanel.this, "Encrypted text is empty!", "Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String decryptedText = rsa.decrypt(encryptedText);
                    outputTextArea.setText(decryptedText);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(RSAPanel.this, "Error decrypting text: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("RSA Encryption/Decryption");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new RSAPanel());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
