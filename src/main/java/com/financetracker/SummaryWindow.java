package com.financetracker;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.math.BigDecimal;

public class SummaryWindow {
    private TransactionManager transactionManager;

    public SummaryWindow(Stage stage, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Get financial summary
        FinancialSummary summary = transactionManager.getFinancialSummary();

        // Create labels
        Label totalIncomeLabel = new Label("Total Income: $" + summary.getTotalIncome());
        Label totalExpensesLabel = new Label("Total Expenses: $" + summary.getTotalExpenses());
        Label balanceLabel = new Label("Balance: $" + summary.getBalance());

        // Add labels to grid
        grid.add(totalIncomeLabel, 0, 0);
        grid.add(totalExpensesLabel, 0, 1);
        grid.add(balanceLabel, 0, 2);

        Scene scene = new Scene(grid, 300, 200);
        stage.setTitle("Financial Summary");
        stage.setScene(scene);
        stage.show();
    }
}