package com.financetracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private int id;  // Add ID field
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String category;
    private TransactionType type;

    public enum TransactionType {
        INCOME, EXPENSE
    }

    // Add constructor with ID for database retrieval
    public Transaction(int id, LocalDate date, String description, BigDecimal amount,
                       String category, TransactionType type) {
        this.id = id;
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = type;
    }

    // Keep existing constructor for new transactions
    public Transaction(LocalDate date, String description, BigDecimal amount,
                       String category, TransactionType type) {
        this(-1, date, description, amount, category, type); // Use -1 for unsaved transactions
    }



    // Existing getters
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public TransactionType getType() { return type; }
    public int getId() { return id; }

    @Override
    public String toString() {
        return String.format("\n%d | %s | %-20s | %-8s | %-10s | %s",
                id, date, description, amount, category, type);
    }
}