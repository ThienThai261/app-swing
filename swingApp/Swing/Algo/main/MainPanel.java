package Algo.main;

import view.SM4Panel;
import view.*;


import javax.swing.*;
import java.awt.*;

public class MainPanel extends JFrame {

    private JPanel rightPanel;  

    public MainPanel() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Test Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 400);
        setLocationRelativeTo(null);

        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        JLabel text1 = new JLabel("Classic Algorithm");
        JLabel text2 = new JLabel("Modern Algorithm");
        JLabel text3 = new JLabel("Hash Algorithm");


        JButton openVigPanelButton = new JButton("Vigenère Cipher");
        JButton openHillButton = new JButton("Hill Cipher");
        JButton openAESButton = new JButton("AES Cipher");
        JButton openRC4Button = new JButton("RC4 Cipher");
        JButton openDESButton = new JButton("DES Cipher");
        JButton openBlowfishButton = new JButton("Blowfish Cipher");
        JButton openCeasarButton = new JButton("Caesar Cipher");
        JButton subButton = new JButton("Substituion Cipher");
        JButton hashButton = new JButton("Hash ");
        JButton GOSTButton = new JButton("GOSTFALSE ");
        JButton SM4Button = new JButton("SM4 ");
        JButton RSAButton = new JButton("RSA");
        JLabel Class = new JLabel("Classic Algo");


        

        openVigPanelButton.addActionListener(e -> openPanel("Vigenère"));
        openHillButton.addActionListener(e -> openPanel("Hill"));
        openAESButton.addActionListener(e -> openPanel("AES"));
        openRC4Button.addActionListener(e -> openPanel("RC4"));
        openDESButton.addActionListener(e -> openPanel("DES"));
        openBlowfishButton.addActionListener(e -> openPanel("Blowfish"));
        openCeasarButton.addActionListener(e -> openPanel("Ceasar"));
        hashButton.addActionListener(e -> openPanel("Hash"));
        GOSTButton.addActionListener(e -> openPanel("GOSTFALSE"));
        SM4Button.addActionListener(e -> openPanel("SM4"));
        subButton.addActionListener(e -> openPanel("Sub"));
        RSAButton.addActionListener(e -> openPanel("RSA"));



        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(15, 1));
        buttonPanel.add(text1);
        buttonPanel.add(openVigPanelButton);
        buttonPanel.add(openHillButton);
        buttonPanel.add(openCeasarButton);
        buttonPanel.add(subButton);
        buttonPanel.add(text2);
        buttonPanel.add(openAESButton);
        buttonPanel.add(openRC4Button);
        buttonPanel.add(openDESButton);
        buttonPanel.add(openBlowfishButton);
        buttonPanel.add(GOSTButton);
        buttonPanel.add(SM4Button);
        buttonPanel.add(RSAButton);
        buttonPanel.add(text3);
        buttonPanel.add(hashButton);



        
        splitPane.setLeftComponent(new JScrollPane(buttonPanel));

        splitPane.setDividerLocation(200);  

        
        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());  
        rightPanel.add(new JLabel("Please choose an algorithm from the left menu"), BorderLayout.CENTER);

        
        splitPane.setRightComponent(rightPanel);

        
        add(splitPane, BorderLayout.CENTER);
    }

    private void openPanel(String panelType) {
        
        JPanel panel = null;
        switch (panelType) {
            case "Vigenère":
                panel = new VigPanel();  
                break;
            case "Hill":
                panel = new HillPanel(); 
                break;
            case "Ceasar":
                panel = new CaesarPanel();  
                break;

            case "AES":
                panel = new AESPanel();  
                break;
            case "RC4":
                panel = new RC4Panel();  
                break;
            case "DES":
                panel = new DESPanel();  
                break;
            case "Blowfish":
                panel = new BlowfishPanel();  
                break;
            case "Hash":
                panel = new HashPanel();  
                break;
            case "Sub":
                panel= new SubstitutionCipherPanel();
                break;
            case "GOSTFALSE":

                panel= new GOSTPanel();
                break;
            case "SM4":
                panel= new SM4Panel();
                break;
            case "RSA":
                panel= new RSAPanel();
                break;
            default:
                throw new IllegalArgumentException("Unknown panel type: " + panelType);
        }

        
        rightPanel.removeAll();
        rightPanel.add(panel, BorderLayout.CENTER);  
        rightPanel.revalidate();
        rightPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainPanel mainPanel = new MainPanel();
            mainPanel.setVisible(true);
        });
    }
}
