package com.ludusimperius.workwise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @GetMapping
    public String showAdminDashboard(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("employees", employeeRepository.findAll());
        return "admin";
    }

    @GetMapping("/departments")
    public String showDepartments(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        var departments = departmentRepository.findAll();
        var employees = employeeRepository.findAll();
        
        Map<Department, java.util.List<Employee>> employeesByDepartment = employees.stream()
            .collect(Collectors.groupingBy(Employee::getDepartment));
            
        model.addAttribute("departments", departments);
        model.addAttribute("employeesByDepartment", employeesByDepartment);
        
        return "departments";
    }

    @GetMapping("/users")
    public String showUsers(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        // Get all users
        var users = userRepository.findAll();
        model.addAttribute("users", users);
        
        // Get all roles
        model.addAttribute("roles", User.Role.values());
        
        // Get employees that are not linked to any user
        var linkedEmployeeIds = users.stream()
            .filter(u -> u.getEmployee() != null)
            .map(u -> u.getEmployee().getId())
            .collect(Collectors.toSet());
        
        var unlinkedEmployees = employeeRepository.findAll().stream()
            .filter(e -> !linkedEmployeeIds.contains(e.getId()))
            .collect(Collectors.toList());
            
        model.addAttribute("unlinkedEmployees", unlinkedEmployees);
        
        return "users";
    }

    @PostMapping("/users/create")
    public String createUser(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam User.Role role,
                           @RequestParam(required = false) Long employeeId,
                           HttpSession session,
                           Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            if (userRepository.existsByUsername(username)) {
                model.addAttribute("error", "Username already exists");
                return showUsers(session, model);
            }

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setRole(role);
            user.setActive(true);
            user.setTemporaryPassword(true);
            user.setLastLogin(LocalDateTime.now());

            if (employeeId != null) {
                Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
                user.setEmployee(employee);
                employee.setUser(user);
            } else if (role == User.Role.EMPLOYEE) {
                // Create new Employee entity for EMPLOYEE role
                Employee employee = new Employee();
                employee.setFullName(username); // Set initial name as username
                user.setEmployee(employee);
                employee.setUser(user);
            }

            // Save user first, which will cascade save the employee
            userRepository.save(user);
            
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating user: " + e.getMessage());
            return showUsers(session, model);
        }
    }

    @PostMapping("/users/reset-password")
    public String resetPassword(@RequestParam Long userId, 
                              HttpSession session, 
                              Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            userRepository.findById(userId).ifPresent(user -> {
                user.setPassword("password123");
                user.setTemporaryPassword(true);
                userRepository.save(user);
            });
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error resetting password: " + e.getMessage());
            return showUsers(session, model);
        }
    }

    @PostMapping("/users/update-role")
    public String updateRole(@RequestParam Long userId,
                           @RequestParam User.Role newRole,
                           HttpSession session,
                           Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            userRepository.findById(userId).ifPresent(user -> {
                user.setRole(newRole);
                userRepository.save(user);
            });
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating role: " + e.getMessage());
            return showUsers(session, model);
        }
    }

    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long userId, 
                           HttpSession session,
                           Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            if (user.getEmployee() != null) {
                Employee employee = user.getEmployee();
                employee.setUser(null);
                employeeRepository.save(employee);
            }
            
            userRepository.delete(user);
            return "redirect:/admin/users";
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting user: " + e.getMessage());
            return showUsers(session, model);
        }
    }

    @GetMapping("/employees/new")
    public String showEmployeeForm(HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("employee", new Employee());
        return "employee_form";
    }

    @GetMapping("/employees/{id}")
    public String editEmployee(@PathVariable Long id, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        var employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            return "redirect:/admin";
        }
        
        model.addAttribute("employee", employeeOpt.get());
        model.addAttribute("departments", departmentRepository.findAll());
        return "employee_form";
    }

    @PostMapping("/employees/save")
    public String saveEmployee(@ModelAttribute Employee employee, HttpSession session, Model model) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        System.out.println("Received employee: " + employee);
        System.out.println("Full Name: " + employee.getFullName());
        System.out.println("Position: " + employee.getPosition());
        System.out.println("Department: " + employee.getDepartment());
        
        // Validate required fields
        if (employee.getFullName() == null || employee.getFullName().trim().isEmpty()) {
            System.out.println("Full Name validation failed");
            model.addAttribute("error", "Full Name is required");
            model.addAttribute("employee", employee);
            model.addAttribute("departments", departmentRepository.findAll());
            return "employee_form";
        }
        
        Employee savedEmployee = employeeRepository.save(employee);
        System.out.println("Saved employee: " + savedEmployee);
        System.out.println("Saved Full Name: " + savedEmployee.getFullName());
        
        return "redirect:/admin";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) {
            return "redirect:/login";
        }
        
        employeeRepository.deleteById(id);
        return "redirect:/admin";
    }

    private boolean isAdmin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user != null && user.getRole() == User.Role.ADMIN;
    }
} 