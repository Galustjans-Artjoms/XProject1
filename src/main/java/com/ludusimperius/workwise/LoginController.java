package com.ludusimperius.workwise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.Period;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", messageSource.getMessage("login.error", null, LocaleContextHolder.getLocale()));
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        logger.debug("Login attempt for username: {}", username);
        
        if (username == null || username.trim().isEmpty()) {
            model.addAttribute("error", messageSource.getMessage("login.username.required", null, LocaleContextHolder.getLocale()));
            return "login";
        }
        
        if (password == null || password.trim().isEmpty()) {
            model.addAttribute("error", messageSource.getMessage("login.password.required", null, LocaleContextHolder.getLocale()));
            return "login";
        }
        
        Optional<User> userOpt = userRepository.findByUsername(username.trim());
        
        if (userOpt.isEmpty()) {
            logger.warn("Login failed - user not found: {}", username);
            model.addAttribute("error", messageSource.getMessage("login.error", null, LocaleContextHolder.getLocale()));
            return "login";
        }
        
        User user = userOpt.get();
        
        if (!user.getPassword().equals(password)) {
            logger.warn("Login failed - invalid password for user: {}", username);
            model.addAttribute("error", messageSource.getMessage("login.error", null, LocaleContextHolder.getLocale()));
            return "login";
        }
        
        if (!user.isActive()) {
            logger.warn("Login failed - user account is inactive: {}", username);
            model.addAttribute("error", messageSource.getMessage("login.account.inactive", null, LocaleContextHolder.getLocale()));
            return "login";
        }

        user.setActive(true);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        session.setAttribute("user", user);
        logger.info("Successful login for username: {}", username);
        
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin";
        }
        
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Add user to model
        model.addAttribute("user", user);

        // Calculate working days in current month
        YearMonth yearMonth = YearMonth.now();
        int workDays = yearMonth.lengthOfMonth() - 8; // Approximate (excluding weekends)
        model.addAttribute("workDays", workDays);

        // Get employee data if exists
        if (user.getEmployee() != null) {
            Employee employee = user.getEmployee();
            model.addAttribute("department", employee.getDepartment());
            
            // Calculate experience
            if (employee.getHireDate() != null) {
                LocalDate now = LocalDate.now();
                LocalDate hireDate = employee.getHireDate();
                Period period = Period.between(hireDate, now);
                model.addAttribute("experience", period.getYears() + "y " + period.getMonths() + "m");
            }
        }

        return "dashboard";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
} 