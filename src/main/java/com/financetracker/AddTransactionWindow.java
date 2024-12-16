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

    // Make fields class members so they can be accessed by prefill method
    private TextField descriptionField;
    private TextField amountField;
    private ComboBox<Transaction.TransactionType> typeComboBox;
    private TextField categoryField;
    private DatePicker datePicker;

    public AddTransactionWindow(Stage stage, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.getStyleClass().add("add-transaction-grid");

        // Initialize form fields
        descriptionField = new TextField();
        descriptionField.getStyleClass().add("text-field");

        amountField = new TextField();
        amountField.getStyleClass().add("text-field");

        typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll(Transaction.TransactionType.values());
        typeComboBox.getStyleClass().add("combo-box");

        categoryField = new TextField();
        categoryField.getStyleClass().add("text-field");

        datePicker = new DatePicker(LocalDate.now());
        datePicker.getStyleClass().add("date-picker");

        Button submitButton = new Button("Add Transaction");
        submitButton.getStyleClass().add("submit-button");

        // Add labels and fields to grid
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.getStyleClass().add("form-label");
        grid.add(descriptionLabel, 0, 0);
        grid.add(descriptionField, 1, 0);

        Label amountLabel = new Label("Amount:");
        amountLabel.getStyleClass().add("form-label");
        grid.add(amountLabel, 0, 1);
        grid.add(amountField, 1, 1);

        Label typeLabel = new Label("Type:");
        typeLabel.getStyleClass().add("form-label");
        grid.add(typeLabel, 0, 2);
        grid.add(typeComboBox, 1, 2);

        Label categoryLabel = new Label("Category:");
        categoryLabel.getStyleClass().add("form-label");
        grid.add(categoryLabel, 0, 3);
        grid.add(categoryField, 1, 3);

        Label dateLabel = new Label("Date:");
        dateLabel.getStyleClass().add("form-label");
        grid.add(dateLabel, 0, 4);
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

        grid.getStyleClass().add("add-transaction-grid");

        Scene scene = new Scene(grid);
        scene.getStylesheets().add("add_transaction_window_styles.css");

        scene.getRoot().getStyleClass().add("add-transaction-window");

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    // Add method to prefill form with existing transaction data
    public void prefillFromTransaction(Transaction transaction) {
        if (transaction != null) {
            datePicker.setValue(transaction.getDate());
            descriptionField.setText(transaction.getDescription());
            amountField.setText(transaction.getAmount().toString());
            categoryField.setText(transaction.getCategory());
            typeComboBox.setValue(transaction.getType());
        }
    }
}