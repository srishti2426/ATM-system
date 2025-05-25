package com.atm.dao;

import com.atm.database.DatabaseConfig;
import com.atm.model.Transaction;
import com.atm.model.Transaction.TransactionType;
import com.atm.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    
    public boolean recordTransaction(Transaction transaction) throws SQLException {
        String query = "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, transaction.getAccountNumber());
            pstmt.setString(2, transaction.getType().toString());
            pstmt.setDouble(3, transaction.getAmount());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setTransactionId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }
    
    public List<Transaction> getTransactionHistory(String accountNumber) throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC LIMIT 10";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction();
                    transaction.setTransactionId(rs.getInt("transaction_id"));
                    transaction.setAccountNumber(rs.getString("account_number"));
                    transaction.setType(TransactionType.valueOf(rs.getString("transaction_type")));
                    transaction.setAmount(rs.getDouble("amount"));
                    transaction.setTransactionDate(rs.getTimestamp("transaction_date").toLocalDateTime());
                    transactions.add(transaction);
                }
            }
        }
        return transactions;
    }
    
    public boolean performTransfer(String fromAccount, String toAccount, double amount) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);
            
            UserDAO userDAO = new UserDAO();
            User fromUser = userDAO.getUser(fromAccount);
            User toUser = userDAO.getUser(toAccount);
            
            if (fromUser == null || toUser == null || fromUser.getBalance() < amount) {
                return false;
            }
            
            // Create withdrawal transaction
            Transaction withdrawal = new Transaction(fromAccount, TransactionType.WITHDRAWAL, amount);
            recordTransaction(withdrawal);
            
            // Create deposit transaction
            Transaction deposit = new Transaction(toAccount, TransactionType.DEPOSIT, amount);
            recordTransaction(deposit);
            
            // Update balances
            userDAO.updateBalance(fromAccount, fromUser.getBalance() - amount);
            userDAO.updateBalance(toAccount, toUser.getBalance() + amount);
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
} 