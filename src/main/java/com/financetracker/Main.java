package com.financetracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    private TransactionManager transactionManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            transactionManager = new TransactionManager();

            VBox root = new VBox(10);
            root.setPadding(new Insets(20));

            // Create and style buttons...
            Button addTransactionBtn = new Button("Add Transaction");
            Button viewTransactionsBtn = new Button("View Transactions");
            Button viewSummaryBtn = new Button("View Summary");

            // Button handlers...
            addTransactionBtn.setOnAction(e -> {
                Stage addTransactionStage = new Stage();
                addTransactionStage.setMinWidth(1280);
                addTransactionStage.setMinHeight(720);
                new AddTransactionWindow(addTransactionStage, transactionManager);
            });

            viewTransactionsBtn.setOnAction(e -> {
                Stage viewTransactionsStage = new Stage();
                viewTransactionsStage.setMinWidth(1280);
                viewTransactionsStage.setMinHeight(720);
                new ViewTransactionsWindow(viewTransactionsStage, transactionManager);
            });

            viewSummaryBtn.setOnAction(e -> {
                Stage summaryStage = new Stage();
                summaryStage.setMinWidth(1280);
                summaryStage.setMinHeight(720);
                new SummaryWindow(summaryStage, transactionManager);
            });

            root.getChildren().addAll(addTransactionBtn, viewTransactionsBtn, viewSummaryBtn);
            root.setAlignment(Pos.CENTER);

            // Create scene with 16:9 ratio
            Scene scene = new Scene(root, 1280, 720);  // 720p resolution

            primaryStage.setTitle("Personal Finance Tracker");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(720);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();  // Detailed error info
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}