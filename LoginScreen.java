package com.atm.ui;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField accountField;
    private JPasswordField pinField;
    private JButton loginButton;
    private UserDAO userDAO;
    
    public LoginScreen() {
        userDAO = new UserDAO();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("ATM System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = UIUtil.createMainPanel();
        
        // Header
        JLabel headerLabel = UIUtil.createHeader("Welcome to ATM System");
        mainPanel.add(headerLabel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIUtil.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Account Number
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Account Number:"), gbc);
        
        gbc.gridx = 1;
        accountField = UIUtil.createTextField();
        formPanel.add(accountField, gbc);
        
        // PIN
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("PIN:"), gbc);
        
        gbc.gridx = 1;
        pinField = UIUtil.createPasswordField();
        formPanel.add(pinField, gbc);
        
        // Login Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginButton = UIUtil.createButton("Login");
        formPanel.add(loginButton, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Add action listener
        loginButton.addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String accountNumber = accountField.getText();
        String pin = new String(pinField.getPassword());
        
        if (accountNumber.isEmpty() || pin.isEmpty()) {
            UIUtil.showError(this, "Please enter both account number and PIN");
            return;
        }
        
        try {
            if (userDAO.authenticate(accountNumber, pin)) {
                User user = userDAO.getUser(accountNumber);
                MainMenu mainMenu = new MainMenu(user);
                mainMenu.setVisible(true);
                this.dispose();
            } else {
                UIUtil.showError(this, "Invalid account number or PIN");
            }
        } catch (SQLException ex) {
            UIUtil.showError(this, "Error connecting to database: " + ex.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginScreen().setVisible(true);
        });
    }
} 