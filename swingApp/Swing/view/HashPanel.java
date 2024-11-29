package view;

import Algo.Hash.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.NoSuchAlgorithmException;

public class HashPanel extends JPanel {
    private JTextField inputTextField;
    private JTextField fileTextField;
    private JComboBox<String> algoComboBox;
    private JTextArea outputTextArea;

    public HashPanel() {
        setLayout(new BorderLayout());

        
        JPanel inputPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        
        inputTextField = new JTextField();
        inputPanel.add(createLabeledPanel("Input Text:", inputTextField));

        
        JPanel filePanel = new JPanel(new BorderLayout());
        fileTextField = new JTextField();
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(e -> browseFile());
        filePanel.add(fileTextField, BorderLayout.CENTER);
        filePanel.add(browseButton, BorderLayout.EAST);
        inputPanel.add(createLabeledPanel("Input File:", filePanel));

        
        String[] algorithms = {"MD5", "SHA-1", "SHA-3","SHA-256","SHA-384","SHA-512"};
        algoComboBox = new JComboBox<>(algorithms);
        inputPanel.add(createLabeledPanel("Choose Hash Algorithm:", algoComboBox));

        add(inputPanel, BorderLayout.NORTH);

        
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output Hash"));

        outputTextArea = new JTextArea(5, 20);
        outputTextArea.setEditable(false);
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        add(outputPanel, BorderLayout.CENTER);

        
        JButton hashButton = new JButton("Hash");
        hashButton.addActionListener(e -> performHash());
        add(hashButton, BorderLayout.SOUTH);
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            fileTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void performHash() {
        String inputText = inputTextField.getText().trim();
        String filePath = fileTextField.getText().trim();
        String selectedAlgo = (String) algoComboBox.getSelectedItem();

        if (inputText.isEmpty() && filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide input text or choose a file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = inputText;
        if (!filePath.isEmpty()) {
            input = readFileContent(filePath);
            if (input == null) {
                JOptionPane.showMessageDialog(this, "Error reading the file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        try {
            String hash = null;
            switch (selectedAlgo) {
                case "MD5":
                    hash = MD5Hasher.hash(input);
                    break;
                case "SHA-1":
                    hash = SHA1Hasher.hash(input);
                    break;
                case "SHA_3":
                    hash = SHA3Hasher.hash(input);
                    break;
                case "SHA-256":
                    hash = SHA256Hasher.hash(input);
                    break;
                case "SHA-384":
                    hash = SHA384Hasher.hash(input);
                    break;
                case "SHA3-512":
                    hash = SHA512Hasher.hash(input);
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Invalid algorithm selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
            }
            outputTextArea.setText(hash);
        } catch (NoSuchAlgorithmException e) {
            JOptionPane.showMessageDialog(this, "Hashing algorithm not supported.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (Exception e) {
            return null;
        }
        return content.toString().trim();
    }

    private JPanel createLabeledPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Hash Panel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new HashPanel());
            frame.setSize(500, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
