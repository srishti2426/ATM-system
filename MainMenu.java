package com.atm.ui;

import com.atm.dao.TransactionDAO;
import com.atm.dao.UserDAO;
import com.atm.model.Transaction;
import com.atm.model.User;
import com.atm.util.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class MainMenu extends JFrame {
    private User currentUser;
    private UserDAO userDAO;
    private TransactionDAO transactionDAO;
    private JLabel balanceLabel;
    
    public MainMenu(User user) {
        this.currentUser = user;
        this.userDAO = new UserDAO();
        this.transactionDAO = new TransactionDAO();
        initializeUI();
        updateBalance();
    }
    
    private void initializeUI() {
        setTitle("ATM System - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = UIUtil.createMainPanel();
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIUtil.BACKGROUND_COLOR);
        
        JLabel welcomeLabel = UIUtil.createHeader("Welcome, " + currentUser.getFirstName());
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        
        balanceLabel = new JLabel();
        balanceLabel.setFont(UIUtil.REGULAR_FONT);
        headerPanel.add(balanceLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        buttonsPanel.setBackground(UIUtil.BACKGROUND_COLOR);
        
        JButton withdrawButton = UIUtil.createButton("Withdraw");
        JButton depositButton = UIUtil.createButton("Deposit");
        JButton transferButton = UIUtil.createButton("Transfer");
        JButton miniStatementButton = UIUtil.createButton("Mini Statement");
        JButton changePinButton = UIUtil.createButton("Change PIN");
        JButton logoutButton = UIUtil.createButton("Logout");
        
        buttonsPanel.add(withdrawButton);
        buttonsPanel.add(depositButton);
        buttonsPanel.add(transferButton);
        buttonsPanel.add(miniStatementButton);
        buttonsPanel.add(changePinButton);
        buttonsPanel.add(logoutButton);
        
        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Add action listeners
        withdrawButton.addActionListener(e -> handleWithdraw());
        depositButton.addActionListener(e -> handleDeposit());
        transferButton.addActionListener(e -> handleTransfer());
        miniStatementButton.addActionListener(e -> handleMiniStatement());
        changePinButton.addActionListener(e -> handleChangePin());
        logoutButton.addActionListener(e -> handleLogout());
    }
    
    private void updateBalance() {
        try {
            currentUser = userDAO.getUser(currentUser.getAccountNumber());
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            balanceLabel.setText("Balance: " + formatter.format(currentUser.getBalance()));
        } catch (SQLException e) {
            UIUtil.showError(this, "Error updating balance: " + e.getMessage());
        }
    }
    
    private void handleWithdraw() {
        String amount = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amount != null && !amount.isEmpty()) {
            try {
                double withdrawAmount = Double.parseDouble(amount);
                if (withdrawAmount <= 0) {
                    UIUtil.showError(this, "Please enter a valid amount");
                    return;
                }
                if (withdrawAmount > currentUser.getBalance()) {
                    UIUtil.showError(this, "Insufficient balance");
                    return;
                }
                
                Transaction transaction = new Transaction(
                    currentUser.getAccountNumber(),
                    Transaction.TransactionType.WITHDRAWAL,
                    withdrawAmount
                );
                
                if (transactionDAO.recordTransaction(transaction)) {
                    userDAO.updateBalance(currentUser.getAccountNumber(), 
                                       currentUser.getBalance() - withdrawAmount);
                    updateBalance();
                    UIUtil.showSuccess(this, "Withdrawal successful");
                }
            } catch (NumberFormatException e) {
                UIUtil.showError(this, "Please enter a valid amount");
            } catch (SQLException e) {
                UIUtil.showError(this, "Error processing withdrawal: " + e.getMessage());
            }
        }
    }
    
    private void handleDeposit() {
        String amount = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amount != null && !amount.isEmpty()) {
            try {
                double depositAmount = Double.parseDouble(amount);
                if (depositAmount <= 0) {
                    UIUtil.showError(this, "Please enter a valid amount");
                    return;
                }
                
                Transaction transaction = new Transaction(
                    currentUser.getAccountNumber(),
                    Transaction.TransactionType.DEPOSIT,
                    depositAmount
                );
                
                if (transactionDAO.recordTransaction(transaction)) {
                    userDAO.updateBalance(currentUser.getAccountNumber(), 
                                       currentUser.getBalance() + depositAmount);
                    updateBalance();
                    UIUtil.showSuccess(this, "Deposit successful");
                }
            } catch (NumberFormatException e) {
                UIUtil.showError(this, "Please enter a valid amount");
            } catch (SQLException e) {
                UIUtil.showError(this, "Error processing deposit: " + e.getMessage());
            }
        }
    }
    
    private void handleTransfer() {
        JTextField accountField = new JTextField();
        JTextField amountField = new JTextField();
        
        Object[] message = {
            "Recipient Account Number:", accountField,
            "Amount:", amountField
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Transfer Money",
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String recipientAccount = accountField.getText();
            try {
                double amount = Double.parseDouble(amountField.getText());
                
                if (amount <= 0) {
                    UIUtil.showError(this, "Please enter a valid amount");
                    return;
                }
                
                if (amount > currentUser.getBalance()) {
                    UIUtil.showError(this, "Insufficient balance");
                    return;
                }
                
                if (transactionDAO.performTransfer(currentUser.getAccountNumber(), 
                                                recipientAccount, amount)) {
                    updateBalance();
                    UIUtil.showSuccess(this, "Transfer successful");
                } else {
                    UIUtil.showError(this, "Transfer failed");
                }
            } catch (NumberFormatException e) {
                UIUtil.showError(this, "Please enter a valid amount");
            } catch (SQLException e) {
                UIUtil.showError(this, "Error processing transfer: " + e.getMessage());
            }
        }
    }
    
    private void handleMiniStatement() {
        try {
            List<Transaction> transactions = transactionDAO.getTransactionHistory(
                currentUser.getAccountNumber()
            );
            
            StringBuilder statement = new StringBuilder();
            statement.append("Recent Transactions:\n\n");
            
            NumberFormat formatter = NumberFormat.getCurrencyInstance();
            for (Transaction t : transactions) {
                statement.append(String.format("%s: %s %s\n",
                    t.getTransactionDate().toString(),
                    t.getType().toString(),
                    formatter.format(t.getAmount())
                ));
            }
            
            JTextArea textArea = new JTextArea(statement.toString());
            textArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 300));
            
            JOptionPane.showMessageDialog(this, scrollPane, "Mini Statement",
                                        JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            UIUtil.showError(this, "Error fetching transaction history: " + e.getMessage());
        }
    }
    
    private void handleChangePin() {
        JPasswordField currentPin = new JPasswordField();
        JPasswordField newPin = new JPasswordField();
        JPasswordField confirmPin = new JPasswordField();
        
        Object[] message = {
            "Current PIN:", currentPin,
            "New PIN:", newPin,
            "Confirm New PIN:", confirmPin
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Change PIN",
                                                 JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            String currentPinStr = new String(currentPin.getPassword());
            String newPinStr = new String(newPin.getPassword());
            String confirmPinStr = new String(confirmPin.getPassword());
            
            try {
                if (!userDAO.authenticate(currentUser.getAccountNumber(), currentPinStr)) {
                    UIUtil.showError(this, "Current PIN is incorrect");
                    return;
                }
                
                if (!newPinStr.equals(confirmPinStr)) {
                    UIUtil.showError(this, "New PINs do not match");
                    return;
                }
                
                if (newPinStr.length() != 4) {
                    UIUtil.showError(this, "PIN must be 4 digits");
                    return;
                }
                
                if (userDAO.updatePin(currentUser.getAccountNumber(), newPinStr)) {
                    UIUtil.showSuccess(this, "PIN changed successfully");
                    handleLogout();
                } else {
                    UIUtil.showError(this, "Failed to change PIN");
                }
            } catch (SQLException e) {
                UIUtil.showError(this, "Error changing PIN: " + e.getMessage());
            }
        }
    }
    
    private void handleLogout() {
        new LoginScreen().setVisible(true);
        this.dispose();
    }
} 