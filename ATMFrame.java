package com.atm.ui;

import com.atm.dao.UserDAO;
import com.atm.model.User;
import com.atm.model.TransactionType;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ATMFrame extends JFrame {
    private UserDAO userDAO;
    private User currentUser;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // UI Components
    private JTextField accountField;
    private JPasswordField pinField;
    private JLabel balanceLabel;
    private JTextField amountField;

    public ATMFrame() {
        userDAO = new UserDAO();
        setupFrame();
        createComponents();
        setupLoginPanel();
        setupMainMenuPanel();
    }

    private void setupFrame() {
        setTitle("ATM System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);
    }

    private void createComponents() {
        accountField = new JTextField(15);
        pinField = new JPasswordField(4);
        balanceLabel = new JLabel("Balance: $0.00");
        amountField = new JTextField(10);
    }

    private void setupLoginPanel() {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add components to login panel
        gbc.gridx = 0; gbc.gridy = 0;
        loginPanel.add(new JLabel("Account Number:"), gbc);
        
        gbc.gridx = 1;
        loginPanel.add(accountField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        loginPanel.add(new JLabel("PIN:"), gbc);
        
        gbc.gridx = 1;
        loginPanel.add(pinField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        loginPanel.add(loginButton, gbc);

        mainPanel.add(loginPanel, "LOGIN");
    }

    private void setupMainMenuPanel() {
        JPanel menuPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add balance display
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        menuPanel.add(balanceLabel, gbc);

        // Add transaction amount field
        gbc.gridy = 1;
        menuPanel.add(new JLabel("Amount: $"), gbc);
        menuPanel.add(amountField, gbc);

        // Add buttons
        gbc.gridwidth = 1;
        gbc.gridy = 2;
        
        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(e -> handleWithdraw());
        menuPanel.add(withdrawButton, gbc);

        gbc.gridx = 1;
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(e -> handleDeposit());
        menuPanel.add(depositButton, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());
        menuPanel.add(logoutButton, gbc);

        mainPanel.add(menuPanel, "MENU");
    }

    private void handleLogin() {
        String accountNumber = accountField.getText();
        String pin = new String(pinField.getPassword());
        
        try {
            if (userDAO.authenticate(accountNumber, pin)) {
                currentUser = userDAO.getUser(accountNumber);
                if (currentUser != null) {
                    balanceLabel.setText(String.format("Balance: $%.2f", currentUser.getBalance()));
                    cardLayout.show(mainPanel, "MENU");
                } else {
                    JOptionPane.showMessageDialog(this, "Error loading user data!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void handleWithdraw() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
                return;
            }
            if (amount > currentUser.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds!");
                return;
            }
            
            double newBalance = currentUser.getBalance() - amount;
            if (userDAO.updateBalance(currentUser.getAccountNumber(), newBalance)) {
                currentUser.setBalance(newBalance);
                balanceLabel.setText(String.format("Balance: $%.2f", newBalance));
                userDAO.recordTransaction(currentUser.getAccountNumber(), TransactionType.WITHDRAWAL, amount);
                JOptionPane.showMessageDialog(this, "Withdrawal successful!");
                amountField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
        }
    }

    private void handleDeposit() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
                return;
            }
            
            double newBalance = currentUser.getBalance() + amount;
            if (userDAO.updateBalance(currentUser.getAccountNumber(), newBalance)) {
                currentUser.setBalance(newBalance);
                balanceLabel.setText(String.format("Balance: $%.2f", newBalance));
                userDAO.recordTransaction(currentUser.getAccountNumber(), TransactionType.DEPOSIT, amount);
                JOptionPane.showMessageDialog(this, "Deposit successful!");
                amountField.setText("");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount!");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Transaction failed: " + ex.getMessage());
        }
    }

    private void handleLogout() {
        currentUser = null;
        accountField.setText("");
        pinField.setText("");
        amountField.setText("");
        cardLayout.show(mainPanel, "LOGIN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ATMFrame().setVisible(true);
        });
    }
} 