package com.atm.model;

public enum TransactionType {
    WITHDRAWAL,
    DEPOSIT,
    TRANSFER;

    @Override
    public String toString() {
        return name();
    }
} 