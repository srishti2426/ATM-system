-- Create database
CREATE DATABASE IF NOT EXISTS atm_system;
USE atm_system;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    account_number VARCHAR(16) PRIMARY KEY,
    pin VARCHAR(4) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(16),
    transaction_type ENUM('WITHDRAWAL', 'DEPOSIT', 'TRANSFER') NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (account_number) REFERENCES users(account_number)
);

-- Insert sample user for testing
INSERT INTO users (account_number, pin, first_name, last_name, balance)
VALUES ('1234567890', '1234', 'John', 'Doe', 1000.00); 