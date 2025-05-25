package com.atm.dao;

import com.atm.model.User;
import com.atm.model.TransactionType;
import com.atm.util.DatabaseConnection;
import java.sql.*;

public class UserDAO {
    public boolean authenticate(String accountNumber, String pin) throws SQLException {
        String query = "SELECT * FROM users WHERE account_number = ? AND pin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, pin);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public User getUser(String accountNumber) throws SQLException {
        String query = "SELECT * FROM users WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getString("account_number"),
                        rs.getString("pin"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getDouble("balance")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateBalance(String accountNumber, double newBalance) throws SQLException {
        String query = "UPDATE users SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            return pstmt.executeUpdate() > 0;
        }
    }

    public void recordTransaction(String accountNumber, TransactionType type, double amount) throws SQLException {
        String query = "INSERT INTO transactions (account_number, transaction_type, amount) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, type.toString().toUpperCase());
            pstmt.setDouble(3, amount);
            
            pstmt.executeUpdate();
        }
    }
} 