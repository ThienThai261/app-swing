package view;

import Algo.Classic.SubstitutionCipher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.Scanner;

public class SubstitutionCipherPanel extends JPanel {

    private JTextField keyField;
    private JTextArea inputTextArea;
    private JTextArea resultTextArea;
    private String currentKey;

    public SubstitutionCipherPanel() {
        setLayout(new BorderLayout());

        
        keyField = new JTextField(26);
        keyField.setEditable(false);
        JButton generateKeyButton = new JButton("Generate Key");
        JButton saveKeyButton = new JButton("Save Key");
        JButton loadKeyButton = new JButton("Load Key");

        inputTextArea = new JTextArea(5, 30);
        resultTextArea = new JTextArea(5, 30);
        resultTextArea.setEditable(false);

        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton saveResultButton = new JButton("Save Result");

        
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new FlowLayout());
        keyPanel.add(new JLabel("Key: "));
        keyPanel.add(keyField);
        keyPanel.add(generateKeyButton);
        keyPanel.add(saveKeyButton);
        keyPanel.add(loadKeyButton);

        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridLayout(2, 1));
        textPanel.add(new JScrollPane(inputTextArea));
        textPanel.add(new JScrollPane(resultTextArea));

        
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new FlowLayout());
        actionPanel.add(encryptButton);
        actionPanel.add(decryptButton);
        actionPanel.add(saveResultButton);

        
        add(keyPanel, BorderLayout.NORTH);
        add(textPanel, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);

        
        generateKeyButton.addActionListener(this::generateKey);
        saveKeyButton.addActionListener(this::saveKey);
        loadKeyButton.addActionListener(this::loadKey);
        encryptButton.addActionListener(this::encryptText);
        decryptButton.addActionListener(this::decryptText);
        saveResultButton.addActionListener(this::saveResult);
    }

    private void generateKey(ActionEvent e) {
        currentKey = SubstitutionCipher.generateRandomSubstitutionKey();
        keyField.setText(currentKey);
        JOptionPane.showMessageDialog(this, "Key generated successfully!");
    }

    private void saveKey(ActionEvent e) {
        if (currentKey == null) {
            JOptionPane.showMessageDialog(this, "No key to save. Please generate a key first.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(currentKey);
                JOptionPane.showMessageDialog(this, "Key saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving key: " + ex.getMessage());
            }
        }
    }

    private void loadKey(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (Scanner scanner = new Scanner(fileChooser.getSelectedFile())) {
                if (scanner.hasNextLine()) {
                    currentKey = scanner.nextLine();
                    keyField.setText(currentKey);
                    JOptionPane.showMessageDialog(this, "Key loaded successfully!");
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading key: " + ex.getMessage());
            }
        }
    }

    private void encryptText(ActionEvent e) {
        if (currentKey == null) {
            JOptionPane.showMessageDialog(this, "No key available. Please generate or load a key first.");
            return;
        }
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input text is empty. Please enter some text.");
            return;
        }
        String encryptedText = SubstitutionCipher.encrypt(text, currentKey);
        resultTextArea.setText(encryptedText);
    }

    private void decryptText(ActionEvent e) {
        if (currentKey == null) {
            JOptionPane.showMessageDialog(this, "No key available. Please generate or load a key first.");
            return;
        }
        String text = inputTextArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input text is empty. Please enter some text.");
            return;
        }
        String decryptedText = SubstitutionCipher.decrypt(text, currentKey);
        resultTextArea.setText(decryptedText);
    }

    private void saveResult(ActionEvent e) {
        String resultText = resultTextArea.getText();
        if (resultText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Result is empty. Nothing to save.");
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter writer = new FileWriter(fileChooser.getSelectedFile())) {
                writer.write(resultText);
                JOptionPane.showMessageDialog(this, "Result saved successfully!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving result: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Substitution Cipher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new SubstitutionCipherPanel());
        frame.pack();
        frame.setVisible(true);
    }
}
