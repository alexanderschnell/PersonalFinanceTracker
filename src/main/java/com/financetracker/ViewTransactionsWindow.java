package com.financetracker;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ViewTransactionsWindow implements TransactionListener {
    private final TransactionManager transactionManager;
    private TableView<Transaction> table;
    private ObservableList<Transaction> data;

    // Declare the labels for displaying the financial summary
    private Label incomeLabel;
    private Label expensesLabel;
    private Label balanceLabel;

    // Constructor accepting only TransactionManager
    public ViewTransactionsWindow(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        transactionManager.addTransactionListener(this);

        // Initialize labels for the financial summary
        incomeLabel = new Label("Income: $0.00");
        expensesLabel = new Label("Expenses: $0.00");
        balanceLabel = new Label("Balance: $0.00");

        // Add CSS classes to labels
        incomeLabel.getStyleClass().add("summary-label");
        expensesLabel.getStyleClass().add("summary-label");
        balanceLabel.getStyleClass().addAll("summary-label", "neutral-balance");

        // Set up the layout and scene for displaying the labels
        HBox summaryLayout = new HBox(30); // Increased spacing between labels
        summaryLayout.setPadding(new Insets(10));
        summaryLayout.setAlignment(Pos.CENTER);
        summaryLayout.getChildren().addAll(incomeLabel, expensesLabel, balanceLabel);

        // Create table
        table = new TableView<>();
        table.getStyleClass().add("transaction-table");

        // Create columns for the transaction table
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(150);

        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setPrefWidth(200);

        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.setPrefWidth(150);

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(150);

        TableColumn<Transaction, Transaction.TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(100);

        // Add action column for buttons
        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            private final Button editButton = new Button("Edit");
            private final HBox buttonContainer = new HBox(5); // 5 pixels spacing between buttons

            {
                // Style buttons
                deleteButton.getStyleClass().add("delete-button");
                editButton.getStyleClass().add("edit-button");

                // Set up button container
                buttonContainer.setAlignment(Pos.CENTER);
                buttonContainer.getChildren().addAll(editButton, deleteButton);

                // Delete button action
                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Transaction");
                    alert.setHeaderText("Delete Transaction");
                    alert.setContentText("Are you sure you want to delete this transaction?");
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            transactionManager.removeTransaction(transaction.getId());
                            refreshTable();
                            updateFinancialSummary();
                        }
                    });
                });

                // Edit button action
                editButton.setOnAction(event -> {
                    Transaction existingTransaction = getTableView().getItems().get(getIndex());
                    Stage stage = new Stage();
                    stage.setTitle("Edit Transaction");
                    AddTransactionWindow editWindow = new AddTransactionWindow(stage, transactionManager);

                    // Pre-fill the form with existing transaction details
                    editWindow.prefillFromTransaction(existingTransaction);

                    stage.show();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });
        actionCol.setPrefWidth(160); // Increased width to accommodate both buttons

        table.getColumns().addAll(dateCol, descriptionCol, amountCol, categoryCol, typeCol, actionCol);

        // Make table columns fill the width of the table
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize the data and refresh the table
        refreshTable();
        // Initialize financial summary
        updateFinancialSummary();
    }

    // Refresh the table
    public void refreshTable() {
        data = FXCollections.observableArrayList(transactionManager.getAllTransactions());
        table.setItems(data);
    }

    // Open the window in a new stage (window)
    public void show() {
        Stage stage = new Stage();
        stage.setTitle("View Transactions");

        // Set up the layout and scene
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        // Add the summary and table to the layout
        HBox summaryLayout = new HBox(30);
        summaryLayout.setAlignment(Pos.CENTER);
        summaryLayout.getChildren().addAll(incomeLabel, expensesLabel, balanceLabel);

        // Make table expand to fill available space
        VBox.setVgrow(table, Priority.ALWAYS);

        layout.getChildren().addAll(summaryLayout, table);

        // Create the scene
        Scene scene = new Scene(layout, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/view_transaction_window_styles.css").toExternalForm());

        // Make stage resizable
        stage.setResizable(true);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        stage.setScene(scene);
        stage.show();
    }

    private void updateFinancialSummary() {
        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expenses = BigDecimal.ZERO;

        // Loop through all transactions and sum income and expenses
        for (Transaction transaction : transactionManager.getAllTransactions()) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            } else if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
                expenses = expenses.add(transaction.getAmount());
            }
        }

        // Calculate the balance
        BigDecimal balance = income.subtract(expenses);

        // Use Platform.runLater to update the labels on the UI thread
        final BigDecimal finalIncome = income;
        final BigDecimal finalExpenses = expenses;
        final BigDecimal finalBalance = balance;

        Platform.runLater(() -> {
            incomeLabel.setText(String.format("Income: $%,.2f", finalIncome));
            expensesLabel.setText(String.format("Expenses: $%,.2f", finalExpenses));
            balanceLabel.setText(String.format("Balance: $%,.2f", finalBalance));

            // Remove existing balance style classes
            balanceLabel.getStyleClass().removeAll("positive-balance", "negative-balance", "neutral-balance");

            // Add appropriate style class based on balance
            if (finalBalance.compareTo(BigDecimal.ZERO) > 0) {
                balanceLabel.getStyleClass().add("positive-balance");
            } else if (finalBalance.compareTo(BigDecimal.ZERO) < 0) {
                balanceLabel.getStyleClass().add("negative-balance");
            } else {
                balanceLabel.getStyleClass().add("neutral-balance");
            }
        });
    }

    public TableView<Transaction> getTable() {
        return table;
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        Platform.runLater(() -> {
            refreshTable();
            updateFinancialSummary();
        });
    }

    @Override
    public void onTransactionRemoved(Transaction transaction) {
        Platform.runLater(() -> {
            refreshTable();
            updateFinancialSummary();
        });
    }
}


