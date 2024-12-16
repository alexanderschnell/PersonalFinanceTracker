package com.financetracker;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.math.BigDecimal;

public class SummaryWindow {
    private TransactionManager transactionManager;

    public SummaryWindow(Stage stage, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;

        // Create main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(20));

        // Create GridPane for summary content
        GridPane grid = new GridPane();
        grid.getStyleClass().add("summary-grid");
        grid.setPadding(new Insets(20));
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        // Get financial summary
        FinancialSummary summary = transactionManager.getFinancialSummary();

        // Create and style title
        Label titleLabel = new Label("Financial Summary");
        titleLabel.getStyleClass().addAll("summary-label", "title-label");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-padding: 0 0 10 0;");

        // Create main summary labels
        Label incomeLabel = new Label(String.format("Total Income: $%,.2f", summary.getTotalIncome()));
        Label expensesLabel = new Label(String.format("Total Expenses: $%,.2f", summary.getTotalExpenses()));
        Label balanceLabel = new Label(String.format("Current Balance: $%,.2f", summary.getBalance()));

        // Apply styling classes
        incomeLabel.getStyleClass().add("summary-label");
        expensesLabel.getStyleClass().add("summary-label");
        balanceLabel.getStyleClass().addAll("summary-label", "summary-balance");

        // Add balance color styling
        if (summary.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            balanceLabel.getStyleClass().add("positive-balance");
        } else if (summary.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            balanceLabel.getStyleClass().add("negative-balance");
        }

        // Add components to grid with proper spacing
        grid.add(titleLabel, 0, 0, 2, 1);
        GridPane.setMargin(titleLabel, new Insets(0, 0, 20, 0));

        grid.add(incomeLabel, 0, 1);
        grid.add(expensesLabel, 0, 2);
        grid.add(balanceLabel, 0, 3);

        // Add grid to main container
        mainContainer.getChildren().add(grid);

        // Create scene
        Scene scene = new Scene(mainContainer, 500, 400);
        scene.getStylesheets().addAll(
                getClass().getResource("/view_summary_styles.css").toExternalForm()
        );

        // Configure stage
        stage.setTitle("Financial Summary");
        stage.setMinWidth(500);
        stage.setMinHeight(400);
        stage.setScene(scene);
        stage.show();

        // Center the window on the screen
        stage.centerOnScreen();
    }
}
