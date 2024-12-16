package com.financetracker;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class Main extends Application implements TransactionListener {
    private TransactionManager transactionManager;
    private BorderPane mainLayout;
    private Label balanceLabel;
    private Label incomeLabel;
    private Label expensesLabel;

    @Override
    public void start(Stage primaryStage) {
        try {
            transactionManager = new TransactionManager();
            transactionManager.addTransactionListener(this); // Register as listener

            // Create main layout
            mainLayout = new BorderPane();

            // Create sidebar
            VBox sidebar = createSidebar();

            // Set initial content (dashboard view)
            VBox mainContent = createMainContent();
            HBox header = createHeader();

            // Add components to main layout
            mainLayout.setLeft(sidebar);
            mainLayout.setCenter(mainContent);
            mainLayout.setTop(header);

            // Create scene
            Scene scene = new Scene(mainLayout, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/main_styles.css").toExternalForm());

            primaryStage.setTitle("Personal Finance Tracker");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1280);
            primaryStage.setMinHeight(720);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // Create header

    private HBox createHeader() {
        HBox header = new HBox();
        header.setPadding(new Insets(20, 20, 20, 48));  // Set left padding to 50
        header.getStyleClass().add("header");

        Label title = new Label("MyFinancePal");
        title.getStyleClass().add("header-title");

        // Set the HGrow to be used by the Label to push it to the right
        HBox.setHgrow(title, Priority.ALWAYS);

        // Align the title to the right
        title.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().add(title);
        return header;
    }


    // Create sidebar
    private VBox createSidebar() {
        VBox sidebar = new VBox(20);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(250);
        sidebar.getStyleClass().add("sidebar");

        Button dashboardBtn = createNavigationButton("Dashboard");
        Button transactionsBtn = createNavigationButton("Transactions");
        Button summaryBtn = createNavigationButton("Summary");
        Button settingsBtn = createNavigationButton("Settings");

        // Add navigation handlers
        dashboardBtn.setOnAction(e -> mainLayout.setCenter(createMainContent()));
        transactionsBtn.setOnAction(e -> mainLayout.setCenter(createTransactionsView()));
        summaryBtn.setOnAction(e -> mainLayout.setCenter(new FinanceDashboard(transactionManager)));
        settingsBtn.setOnAction(e -> mainLayout.setCenter(createSettingsView()));

        sidebar.getChildren().addAll(dashboardBtn, transactionsBtn, summaryBtn, settingsBtn);
        return sidebar;
    }

    // Create main content dashboard
    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));
        mainContent.getStyleClass().add("main-content");

        // Initialize financial summary labels
        balanceLabel = new Label();
        incomeLabel = new Label();
        expensesLabel = new Label();

        // Refresh content initially
        refreshFinancialSummary();

        // Create summary cards with labels
        HBox summaryCards = new HBox(20);
        summaryCards.getChildren().addAll(
                createSummaryCard("Total Balance", balanceLabel),
                createSummaryCard("Income", incomeLabel),
                createSummaryCard("Expenses", expensesLabel)
        );

        // Quick actions section
        VBox quickActions = new VBox(10);
        Label quickActionsLabel = new Label("Quick Actions");
        quickActionsLabel.getStyleClass().add("section-header");

        // Create quick action buttons
        Button addTransactionBtn = createActionButton("Add Transaction");
        Button viewTransactionsBtn = createActionButton("View Transactions");

        // Add handlers for buttons
        addTransactionBtn.setOnAction(e -> {
            openWindow(new Stage(), "Add Transaction",
                    stage -> new AddTransactionWindow(stage, transactionManager));
        });

        viewTransactionsBtn.setOnAction(e -> {
            ViewTransactionsWindow transactionsWindow = new ViewTransactionsWindow(transactionManager);
            transactionsWindow.show();
        });

        // Add buttons to the container
        HBox buttonContainer = new HBox(10);
        buttonContainer.getChildren().addAll(addTransactionBtn, viewTransactionsBtn);

        quickActions.getChildren().addAll(quickActionsLabel, buttonContainer);
        mainContent.getChildren().addAll(summaryCards, quickActions);

        return mainContent;
    }

    // Transactions View
    private VBox createTransactionsView() {
        VBox transactionsView = new VBox(20);
        transactionsView.setPadding(new Insets(20, 40, 60, 40));
        transactionsView.getStyleClass().add("main-content");

        Label title = new Label("Transactions");
        title.getStyleClass().add("section-header");

        ViewTransactionsWindow viewTransactionsWindow = new ViewTransactionsWindow(transactionManager);
        TableView<Transaction> transactionsTable = viewTransactionsWindow.getTable();
        viewTransactionsWindow.refreshTable();

        // Make table expand to fill available space
        VBox.setVgrow(transactionsTable, Priority.ALWAYS);

        // Make sure the CSS is loaded
        transactionsView.getStylesheets().add(getClass().getResource("/view_transaction_window_styles.css").toExternalForm());

        transactionsView.getChildren().addAll(title, transactionsTable);
        return transactionsView;
    }

    // Settings View
    private VBox createSettingsView() {
        VBox settingsView = new VBox(20);
        settingsView.setPadding(new Insets(20));
        settingsView.getStyleClass().add("main-content");

        Label title = new Label("Settings");
        title.getStyleClass().add("section-header");

        Label placeholder = new Label("Settings options will go here.");

        settingsView.getChildren().addAll(title, placeholder);
        return settingsView;
    }

    private void refreshFinancialSummary() {
        FinancialSummary financialSummary = transactionManager.getFinancialSummary();
        BigDecimal totalIncome = financialSummary.getTotalIncome();
        BigDecimal totalExpenses = financialSummary.getTotalExpenses();
        BigDecimal totalBalance = totalIncome.subtract(totalExpenses);

        Platform.runLater(() -> {
            // Update labels dynamically
            balanceLabel.setText(String.format("$%,.2f", totalBalance));
            incomeLabel.setText(String.format("$%,.2f", totalIncome));
            expensesLabel.setText(String.format("$%,.2f", totalExpenses));
        });
    }

    // Implement TransactionListener methods
    @Override
    public void onTransactionAdded(Transaction transaction) {
        refreshFinancialSummary();
    }

    @Override
    public void onTransactionRemoved(Transaction transaction) {
        refreshFinancialSummary();
    }

    private VBox createSummaryCard(String title, Label valueLabel) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("summary-card");
        card.setPrefWidth(200);

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("card-title");
        valueLabel.getStyleClass().add("card-value");

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Button createNavigationButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("action-button");
        return button;
    }

    private void openWindow(Stage stage, String title, java.util.function.Consumer<Stage> windowCreator) {
        stage.setTitle(title);
        windowCreator.accept(stage);
        stage.sizeToScene();
        stage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
