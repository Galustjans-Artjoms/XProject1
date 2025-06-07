package com.ludusimperius.workwise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.DayOfWeek;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/admin/finance")
public class FinanceController {
    private static final Logger logger = LoggerFactory.getLogger(FinanceController.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public String showFinanceDashboard(HttpSession session, Model model) {
        logger.info("Accessing finance dashboard");
        
        User user = (User) session.getAttribute("user");
        if (user == null || user.getRole() != User.Role.ADMIN) {
            logger.warn("Unauthorized access attempt to finance dashboard");
            return "redirect:/login";
        }
        logger.debug("User authorized: {}", user.getUsername());

        List<Employee> employees = employeeRepository.findAll();
        logger.info("Found {} employees", employees.size());
        
        // Log employee details for debugging
        for (Employee emp : employees) {
            logger.debug("Employee: {}, Salary: {}, Department: {}", 
                emp.getFullName(), 
                emp.getSalary(), 
                emp.getDepartment() != null ? emp.getDepartment().getName() : "No Department");
        }
        
        model.addAttribute("employees", employees);
        
        // Calculate current month's working days
        int workingDays = calculateWorkingDays(YearMonth.now());
        model.addAttribute("workingDays", workingDays);
        logger.debug("Working days in current month: {}", workingDays);
        
        return "finance";
    }

    @PostMapping("/calculate/{employeeId}")
    @ResponseBody
    public CalculationResult calculateSalary(@PathVariable Long employeeId) {
        try {
            logger.info("Calculating salary for employee ID: {}", employeeId);
            
            Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
            
            // Get base salary
            BigDecimal baseSalary = employee.getSalary() != null ? employee.getSalary() : BigDecimal.ZERO;
            logger.debug("Base salary: {}", baseSalary);
            
            // Calculate current month's working days
            int workingDays = calculateWorkingDays(YearMonth.now());
            
            // Calculate daily rate
            BigDecimal dailyRate = baseSalary.divide(BigDecimal.valueOf(workingDays), 2, BigDecimal.ROUND_HALF_UP);
            logger.debug("Daily rate: {}", dailyRate);
            
            // Calculate tax (20%)
            BigDecimal tax = baseSalary.multiply(BigDecimal.valueOf(0.20));
            logger.debug("Tax amount: {}", tax);
            
            // Calculate net salary
            BigDecimal netSalary = baseSalary.subtract(tax);
            logger.debug("Net salary: {}", netSalary);
            
            CalculationResult result = new CalculationResult(
                baseSalary,
                dailyRate,
                tax,
                netSalary,
                workingDays
            );
            
            logger.info("Salary calculation completed successfully for employee ID: {}", employeeId);
            return result;
            
        } catch (Exception e) {
            logger.error("Error calculating salary for employee ID: " + employeeId, e);
            throw new RuntimeException("Error calculating salary: " + e.getMessage());
        }
    }

    private int calculateWorkingDays(YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();
        int workingDays = 0;
        
        for (LocalDate date = firstDay; !date.isAfter(lastDay); date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        
        return workingDays;
    }
} 