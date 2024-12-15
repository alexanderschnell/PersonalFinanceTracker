package com.financetracker;

import java.math.BigDecimal;

public class FinancialSummary {

    private final BigDecimal totalIncome;
    private final BigDecimal totalExpenses;

    public FinancialSummary(BigDecimal totalIncome, BigDecimal totalExpenses) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public BigDecimal getBalance() { return totalIncome.subtract(totalExpenses); }

}
