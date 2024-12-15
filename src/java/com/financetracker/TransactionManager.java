package com.financetracker;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private final Connection connection;

    public TransactionManager() {
        try {
            // Connect to SQLite database
            String url = "jdbc:sqlite:C:/JavaProjects/PersonalFinanceTracker/financeTracker/finance_tracker.sqlite";
            this.connection = DriverManager.getConnection(url);

            // Create table if it doesn't exist
            createTableIfNotExists();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        String sql = """
        CREATE TABLE IF NOT EXISTS transactions (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            date TEXT NOT NULL,
            description TEXT NOT NULL,
            amount DECIMAL(10,2) NOT NULL,
            category TEXT NOT NULL,
            type TEXT NOT NULL
        )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error creating transactions table: " + e.getMessage());
        }
    }

    public void addTransaction(Transaction transaction) {
        String sql = "INSERT INTO transactions (date, description, amount, category, type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getDate().toString());
            pstmt.setString(2, transaction.getDescription());
            pstmt.setBigDecimal(3, transaction.getAmount());
            pstmt.setString(4, transaction.getCategory());
            pstmt.setString(5, transaction.getType().toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding transaction: " + e.getMessage());
        }
    }

    public void removeTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing transaction: " + e.getMessage());
        }
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions ORDER BY date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getInt("id"),  // Add this line
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getString("category"),
                        Transaction.TransactionType.valueOf(rs.getString("type"))
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving transactions: " + e.getMessage());
        }
        return transactions;
    }

    public FinancialSummary getFinancialSummary() {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction transaction : getAllTransactions()) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpenses = totalExpenses.add(transaction.getAmount());
            }
        }
        return new FinancialSummary(totalIncome, totalExpenses);
    }

    // Make sure to close the connection when done
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error closing database connection: " + e.getMessage());
        }
    }
}