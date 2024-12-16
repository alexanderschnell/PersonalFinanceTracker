package com.financetracker;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.application.Platform;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;
import java.util.stream.Collectors;

public class FinanceDashboard extends VBox implements TransactionListener {
    private TransactionManager transactionManager;
    private PieChart expenseChart;
    private LineChart<Number, Number> trendChart;

    public FinanceDashboard(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        transactionManager.addTransactionListener(this);
        setPadding(new Insets(20));
        setSpacing(20);
        getStyleClass().add("dashboard");
        setupDashboard();
    }

    private void setupDashboard() {
        Label title = new Label("Financial Overview");
        title.getStyleClass().add("dashboard-title");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Create a grid for charts
        GridPane chartsGrid = new GridPane();
        chartsGrid.setHgap(20);
        chartsGrid.setVgap(20);
        chartsGrid.setPadding(new Insets(5));
        chartsGrid.setAlignment(Pos.CENTER);

        // Create and position charts
        expenseChart = createExpenseDistributionChart();
        trendChart = createTrendChart();

        // Add title for each chart
        VBox expenseChartBox = new VBox(10);
        Label expenseChartTitle = new Label("Expense Distribution");
        expenseChartTitle.getStyleClass().add("chart-title");
        expenseChartBox.getChildren().addAll(expenseChartTitle, expenseChart);

        VBox trendChartBox = new VBox(10);
        Label trendChartTitle = new Label("Monthly Income vs Expenses");
        trendChartTitle.getStyleClass().add("chart-title");
        trendChartBox.getChildren().addAll(trendChartTitle, trendChart);

        // Add charts to grid
        chartsGrid.add(expenseChartBox, 0, 0);
        chartsGrid.add(trendChartBox, 1, 0);

        // Add financial summary section
        VBox summaryBox = createFinancialSummaryBox();

        // Add all components to main layout
        getChildren().addAll(title, summaryBox, chartsGrid);
    }

    private VBox createFinancialSummaryBox() {
        VBox summaryBox = new VBox(10);
        summaryBox.setAlignment(Pos.CENTER);
        summaryBox.setPadding(new Insets(0, 20, 20, 20));  // Adjust padding (top, right, bottom, left)
        summaryBox.getStyleClass().add("summary-box");

        // Calculate summary values
        BigDecimal totalIncome = getTotalIncome();
        BigDecimal totalExpenses = getTotalExpenses();
        BigDecimal balance = totalIncome.subtract(totalExpenses);

        // Style labels for income, expenses, and balance
        Label incomeLabel = new Label(String.format("Total Income: $%,.2f", totalIncome));
        incomeLabel.getStyleClass().add("summary-label");
        incomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label expensesLabel = new Label(String.format("Total Expenses: $%,.2f", totalExpenses));
        expensesLabel.getStyleClass().add("summary-label");
        expensesLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label balanceLabel = new Label(String.format("Current Balance: $%,.2f", balance));
        balanceLabel.getStyleClass().add("summary-label");
        balanceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Add color for positive or negative balance
        if (balance.compareTo(BigDecimal.ZERO) > 0) {
            balanceLabel.setStyle(balanceLabel.getStyle() + " -fx-text-fill: #388E3C;");  // Green for positive balance
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            balanceLabel.setStyle(balanceLabel.getStyle() + " -fx-text-fill: #D32F2F;");  // Red for negative balance
        } else {
            balanceLabel.setStyle(balanceLabel.getStyle() + " -fx-text-fill: #1976D2;");  // Blue for zero balance
        }

        summaryBox.getChildren().addAll(incomeLabel, expensesLabel, balanceLabel);
        return summaryBox;
    }


    private PieChart createExpenseDistributionChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Expense Categories");
        pieChart.setLabelsVisible(true);

        Map<String, BigDecimal> categoryTotals = transactionManager.getAllTransactions().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)
                ));

        categoryTotals.forEach((category, total) ->
                pieChart.getData().add(new PieChart.Data(
                        String.format("%s ($%,.2f)", category, total),
                        total.doubleValue()
                ))
        );

        return pieChart;
    }

    private LineChart<Number, Number> createTrendChart() {
        final NumberAxis xAxis = new NumberAxis(1, 12, 1);
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");
        yAxis.setLabel("Amount ($)");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Income vs Expenses by Month");

        XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");
        XYChart.Series<Number, Number> expenseSeries = new XYChart.Series<>();
        expenseSeries.setName("Expenses");

        Map<Integer, BigDecimal> monthlyIncome = transactionManager.getAllTransactions().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonthValue(),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)
                ));

        Map<Integer, BigDecimal> monthlyExpenses = transactionManager.getAllTransactions().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .collect(Collectors.groupingBy(
                        t -> t.getDate().getMonthValue(),
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)
                ));

        // Add data points for each month
        for (int month = 1; month <= 12; month++) {
            incomeSeries.getData().add(new XYChart.Data<>(
                    month,
                    monthlyIncome.getOrDefault(month, BigDecimal.ZERO).doubleValue()
            ));
            expenseSeries.getData().add(new XYChart.Data<>(
                    month,
                    monthlyExpenses.getOrDefault(month, BigDecimal.ZERO).doubleValue()
            ));
        }

        lineChart.getData().addAll(incomeSeries, expenseSeries);
        return lineChart;
    }

    private BigDecimal getTotalIncome() {
        return transactionManager.getAllTransactions().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalExpenses() {
        return transactionManager.getAllTransactions().stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void onTransactionAdded(Transaction transaction) {
        Platform.runLater(this::updateDashboard);
    }

    @Override
    public void onTransactionRemoved(Transaction transaction) {
        Platform.runLater(this::updateDashboard);
    }

    private void updateDashboard() {
        // Clear existing data
        expenseChart.getData().clear();
        trendChart.getData().clear();

        // Recreate charts with updated data
        PieChart newExpenseChart = createExpenseDistributionChart();
        expenseChart.getData().addAll(newExpenseChart.getData());

        LineChart<Number, Number> newTrendChart = createTrendChart();
        trendChart.getData().addAll(newTrendChart.getData());

        // Update summary box
        VBox summaryBox = createFinancialSummaryBox();
        getChildren().set(1, summaryBox);
    }
}