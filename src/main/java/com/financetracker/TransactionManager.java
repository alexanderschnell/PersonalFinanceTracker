package com.financetracker;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private final Connection connection;

    public TransactionManager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Don't need to create directories for a file in root project folder
            String url = "jdbc:sqlite:finance_tracker.sqlite";
            this.connection = DriverManager.getConnection(url);

            // Create table if it doesn't exist
            createTableIfNotExists();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC driver not found: " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to connect to database: " + e.getMessage());
        }
    }

    private List<TransactionListener> listeners = new ArrayList<>();

    public void addTransactionListener(TransactionListener listener) {
        listeners.add(listener);
    }

    public void notifyTransactionAdded(Transaction transaction) {
        for (TransactionListener listener : listeners) {
            listener.onTransactionAdded(transaction);
        }
    }

    public void notifyTransactionRemoved(Transaction transaction) {
        for (TransactionListener listener : listeners) {
            listener.onTransactionRemoved(transaction);
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

            // Notify listeners after adding the transaction
            notifyTransactionAdded(transaction);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            pstmt.executeUpdate();

            // Notify listeners after removing the transaction
            notifyTransactionRemoved(new Transaction(transactionId, LocalDate.now(), "", BigDecimal.ZERO, "", Transaction.TransactionType.EXPENSE));
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void addTransactionListener(ViewTransactionsWindow viewTransactionsWindow) {

    }
}