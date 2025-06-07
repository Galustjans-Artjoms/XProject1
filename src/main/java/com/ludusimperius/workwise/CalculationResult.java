package com.ludusimperius.workwise;

import java.math.BigDecimal;

public class CalculationResult {
    private BigDecimal baseSalary;
    private BigDecimal dailyRate;
    private BigDecimal tax;
    private BigDecimal netSalary;
    private int workingDays;

    public CalculationResult(BigDecimal baseSalary, BigDecimal dailyRate, 
                           BigDecimal tax, BigDecimal netSalary, int workingDays) {
        this.baseSalary = baseSalary;
        this.dailyRate = dailyRate;
        this.tax = tax;
        this.netSalary = netSalary;
        this.workingDays = workingDays;
    }

    // Getters and Setters
    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public int getWorkingDays() { return workingDays; }
    public void setWorkingDays(int workingDays) { this.workingDays = workingDays; }
} 