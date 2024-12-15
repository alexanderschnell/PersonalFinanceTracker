package com.financetracker;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class FinanceDashboard extends VBox {
    private Connection connection; // Your SQLite connection

    public FinanceDashboard(Connection connection) {
        this.connection = connection;
        setPadding(new Insets(10));
        setSpacing(10);
        setupDashboard();
    }

    private void setupDashboard() {
        TabPane tabPane = new TabPane();

        // Overview Tab
        Tab overviewTab = new Tab("Overview");
        overviewTab.setClosable(false);
        GridPane overviewGrid = new GridPane();
        overviewGrid.setHgap(10);
        overviewGrid.setVgap(10);
        overviewGrid.setPadding(new Insets(10));

        // Add expense distribution chart
        PieChart expenseChart = createExpenseDistributionChart();
        overviewGrid.add(expenseChart, 0, 0);

        // Add income vs expenses trend
        LineChart<Number, Number> trendChart = createTrendChart();
        overviewGrid.add(trendChart, 1, 0);

        overviewTab.setContent(overviewGrid);

        // Transactions Tab
        Tab transactionsTab = new Tab("Transactions");
        transactionsTab.setClosable(false);
        transactionsTab.setContent(createTransactionsView());

        tabPane.getTabs().addAll(overviewTab, transactionsTab);
        getChildren().add(tabPane);
    }

    private PieChart createExpenseDistributionChart() {
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Expense Distribution");

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT category, SUM(amount) as total " +
                            "FROM transactions " +
                            "WHERE type = 'expense' " +
                            "GROUP BY category"
            );

            while (rs.next()) {
                pieChart.getData().add(
                        new PieChart.Data(
                                rs.getString("category"),
                                rs.getDouble("total")
                        )
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pieChart;
    }

    private LineChart<Number, Number> createTrendChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Month");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Monthly Trend");

        // Income series
        XYChart.Series<Number, Number> incomeSeries = new XYChart.Series<>();
        incomeSeries.setName("Income");

        // Expenses series
        XYChart.Series<Number, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Expenses");

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT strftime('%m', date) as month, " +
                            "SUM(CASE WHEN type = 'income' THEN amount ELSE 0 END) as income, " +
                            "SUM(CASE WHEN type = 'expense' THEN amount ELSE 0 END) as expenses " +
                            "FROM transactions " +
                            "GROUP BY month " +
                            "ORDER BY month"
            );

            while (rs.next()) {
                int month = rs.getInt("month");
                incomeSeries.getData().add(new XYChart.Data<>(month, rs.getDouble("income")));
                expensesSeries.getData().add(new XYChart.Data<>(month, rs.getDouble("expenses")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        lineChart.getData().addAll(incomeSeries, expensesSeries);
        return lineChart;
    }

    private VBox createTransactionsView() {
        // Create your transactions table view here
        // This is a placeholder for your existing transactions view
        return new VBox();
    }
}