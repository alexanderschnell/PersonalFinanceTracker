package financeTracker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class UserInterface {

    private final Scanner scanner;
    private final TransactionManager transactionManager;

    public UserInterface() {
        scanner = new Scanner(System.in);
        transactionManager = new TransactionManager();
    }

    public void displayMenu() {
        while (true) {
            System.out.println("\nPersonal Finance Tracker\n");
            System.out.println("1. Add Transaction");
            System.out.println("2. View Transactions");
            System.out.println("3. View Summary");
            System.out.println("4. Exit\n");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    viewTransactions();
                    break;
                case 3:
                    viewSummary();
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void addTransaction() {
        System.out.println("\nAdd New Transaction");
        System.out.println("-------------------");

        // Get transaction type
        System.out.print("Type (1 for Income, 2 for Expense): ");
        Transaction.TransactionType type = scanner.nextInt() == 1 ?
                Transaction.TransactionType.INCOME : Transaction.TransactionType.EXPENSE;
        scanner.nextLine(); // Consume newline

        // Get description
        System.out.print("Description: ");
        String description = scanner.nextLine();

        // Get amount
        System.out.print("Amount: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        // Get category
        System.out.print("Category: ");
        String category = scanner.nextLine();

        // Create and add transaction
        Transaction transaction = new Transaction(
                LocalDate.now(), description, amount, category, type
        );
        transactionManager.addTransaction(transaction);
        System.out.println("Transaction added successfully!");
    }

    private void viewTransactions() {
        var transactions = transactionManager.getAllTransactions();
        if (transactions.isEmpty()) {
            System.out.println("\nNo transactions to display.");
            return;
        }

        System.out.println("\nAll Transactions");
        System.out.println("----------------");
        for (Transaction t : transactions) {
            System.out.println(t);
        }
    }

    public void viewSummary() {

        FinancialSummary summary = transactionManager.getFinancialSummary();

        System.out.println("\nFinancial Summary");
        System.out.println("----------------");
        System.out.printf("Total Income:   $%10.2f%n", summary.getTotalIncome());
        System.out.printf("Total Expenses: $%10.2f%n", summary.getTotalExpenses());
        System.out.printf("Balance:        $%10.2f%n", summary.getBalance());
    }
}
