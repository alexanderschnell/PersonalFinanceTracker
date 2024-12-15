package com.financetracker;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AddTransactionWindow {
    private TransactionManager transactionManager;

    public AddTransactionWindow(Stage stage, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Create form fields
        TextField descriptionField = new TextField();
        TextField amountField = new TextField();
        ComboBox<Transaction.TransactionType> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(Transaction.TransactionType.values());
        TextField categoryField = new TextField();
        DatePicker datePicker = new DatePicker(LocalDate.now());

        Button submitButton = new Button("Add Transaction");

        // Add labels and fields to grid
        grid.add(new Label("Description:"), 0, 0);
        grid.add(descriptionField, 1, 0);
        grid.add(new Label("Amount:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Type:"), 0, 2);
        grid.add(typeComboBox, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(categoryField, 1, 3);
        grid.add(new Label("Date:"), 0, 4);
        grid.add(datePicker, 1, 4);
        grid.add(submitButton, 1, 5);

        // Handle submit button click
        submitButton.setOnAction(e -> {
            try {
                LocalDate date = datePicker.getValue();
                String description = descriptionField.getText();
                BigDecimal amount = new BigDecimal(amountField.getText());
                String category = categoryField.getText();
                Transaction.TransactionType type = typeComboBox.getValue();

                if (description.isEmpty() || category.isEmpty() || type == null) {
                    throw new IllegalArgumentException("Please fill in all fields");
                }

                Transaction transaction = new Transaction(
                        date,
                        description,
                        amount,
                        category,
                        type
                );

                transactionManager.addTransaction(transaction);

                // Show success message
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setContentText("Transaction added successfully!");
                alert.showAndWait();

                // Close the window
                stage.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please enter a valid amount!");
                alert.showAndWait();
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Error adding transaction: " + ex.getMessage());
                alert.showAndWait();
            }
        });

        Scene scene = new Scene(grid, 400, 300);
        stage.setTitle("Add Transaction");
        stage.setScene(scene);
        stage.show();
    }
}