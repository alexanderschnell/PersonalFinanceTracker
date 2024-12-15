package financeTracker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {

    private List<Transaction> transactions;

    public TransactionManager() {
        transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions); // Return a copy to protect encapsulation
    }

    public FinancialSummary getFinancialSummary(){
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == Transaction.TransactionType.INCOME) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpenses = totalExpenses.add(transaction.getAmount());
            }
        }
        return new FinancialSummary(totalIncome, totalExpenses);
    }
}
