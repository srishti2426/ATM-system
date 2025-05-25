package com.atm.model;

import java.time.LocalDateTime;

public class Transaction {
    private int transactionId;
    private String accountNumber;
    private TransactionType type;
    private double amount;
    private LocalDateTime transactionDate;

    public enum TransactionType {
        WITHDRAWAL,
        DEPOSIT,
        TRANSFER
    }

    public Transaction() {}

    public Transaction(String accountNumber, TransactionType type, double amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", accountNumber='" + accountNumber + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                '}';
    }
} 