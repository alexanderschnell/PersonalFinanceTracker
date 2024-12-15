package com.financetracker;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ViewTransactionsWindow {
    private TransactionManager transactionManager;
    private TableView<Transaction> table;

    public ViewTransactionsWindow(Stage stage, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        // Create table
        table = new TableView<>();

        // Create columns
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Transaction, BigDecimal> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Transaction, Transaction.TransactionType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        // Add delete button column
        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Actions");
        actionCol.setCellFactory(column -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    // Show confirmation dialog
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Delete Transaction");
                    alert.setHeaderText("Delete Transaction");
                    alert.setContentText("Are you sure you want to delete this transaction?");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            transactionManager.removeTransaction(transaction.getId());
                            refreshTable();
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        // Add columns to table
        table.getColumns().addAll(dateCol, descriptionCol, amountCol, categoryCol, typeCol, actionCol);

        // Add data to table
        refreshTable();

        // Create layout
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(table);
        Scene scene = new Scene(vbox, 800, 400);

        stage.setTitle("View Transactions");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshTable() {
        table.getItems().clear();
        table.getItems().addAll(transactionManager.getAllTransactions());
    }
}