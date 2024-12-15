package financeTracker;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String category;
    private TransactionType type;

    public enum TransactionType {
        INCOME, EXPENSE
    }

    public Transaction(LocalDate date, String description, BigDecimal amount,
                       String category, TransactionType type) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = type;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public BigDecimal getAmount() { return amount; }
    public String getCategory() { return category; }
    public TransactionType getType() { return type; }

    @Override
    public String toString() {
        return String.format("\n%s | %-20s | %-8s | %-10s | %s",
                date, description, amount, category, type);
    }
}