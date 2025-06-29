package com.ludusimperius.workwise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    private Random random = new Random();

    @Override
    public void run(String... args) {
        // Create admin user if not exists
        if (!userRepository.existsByUsername("admin")) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin123");
            adminUser.setRole(User.Role.ADMIN);
            adminUser.setActive(true);
            userRepository.save(adminUser);
        }

        // Create departments if not exist
        Department itDept = null;
        Department hrDept = null;
        Department financeDept = null;
        Department marketingDept = null;
        Department salesDept = null;
        Department researchDept = null;
        
        if (departmentRepository.count() == 0) {
            itDept = createDepartment("IT Department");
            hrDept = createDepartment("HR Department");
            financeDept = createDepartment("Finance Department");
            marketingDept = createDepartment("Marketing Department");
            salesDept = createDepartment("Sales Department");
            researchDept = createDepartment("Research & Development");
        } else {
            itDept = departmentRepository.findByName("IT Department").orElse(null);
            hrDept = departmentRepository.findByName("HR Department").orElse(null);
            financeDept = departmentRepository.findByName("Finance Department").orElse(null);
            marketingDept = departmentRepository.findByName("Marketing Department").orElse(null);
            salesDept = departmentRepository.findByName("Sales Department").orElse(null);
            researchDept = departmentRepository.findByName("Research & Development").orElse(null);
        }

        // Create test employees if none exist
        if (employeeRepository.count() == 0) {
            // IT Department
            createEmployee("John Smith", "Senior Software Engineer", new BigDecimal("5500.00"), itDept, "john.smith@workwise.com", 2);
            createEmployee("Emma Wilson", "Frontend Developer", new BigDecimal("4200.00"), itDept, "emma.wilson@workwise.com", 1);
            createEmployee("Michael Chen", "Backend Developer", new BigDecimal("4500.00"), itDept, "michael.chen@workwise.com", 1);
            createEmployee("David Kumar", "DevOps Engineer", new BigDecimal("5000.00"), itDept, "david.kumar@workwise.com", 2);

            // HR Department
            createEmployee("Sarah Johnson", "HR Manager", new BigDecimal("4800.00"), hrDept, "sarah.johnson@workwise.com", 3);
            createEmployee("Lisa Brown", "HR Specialist", new BigDecimal("3800.00"), hrDept, "lisa.brown@workwise.com", 1);
            createEmployee("James Wilson", "Recruitment Specialist", new BigDecimal("3900.00"), hrDept, "james.wilson@workwise.com", 2);

            // Finance Department
            createEmployee("Mike Anderson", "Senior Accountant", new BigDecimal("4700.00"), financeDept, "mike.anderson@workwise.com", 4);
            createEmployee("Jennifer Lee", "Financial Analyst", new BigDecimal("4200.00"), financeDept, "jennifer.lee@workwise.com", 2);
            createEmployee("Robert Taylor", "Budget Specialist", new BigDecimal("4000.00"), financeDept, "robert.taylor@workwise.com", 1);

            // Marketing Department
            createEmployee("Emily Davis", "Marketing Director", new BigDecimal("5200.00"), marketingDept, "emily.davis@workwise.com", 3);
            createEmployee("Daniel White", "Content Manager", new BigDecimal("4100.00"), marketingDept, "daniel.white@workwise.com", 2);
            createEmployee("Sophie Martin", "Social Media Specialist", new BigDecimal("3800.00"), marketingDept, "sophie.martin@workwise.com", 1);
            createEmployee("Alex Turner", "Digital Marketing Specialist", new BigDecimal("4000.00"), marketingDept, "alex.turner@workwise.com", 2);

            // Sales Department
            createEmployee("Christopher Moore", "Sales Director", new BigDecimal("5500.00"), salesDept, "chris.moore@workwise.com", 4);
            createEmployee("Amanda Clark", "Sales Manager", new BigDecimal("4600.00"), salesDept, "amanda.clark@workwise.com", 2);
            createEmployee("Ryan Phillips", "Sales Representative", new BigDecimal("3900.00"), salesDept, "ryan.phillips@workwise.com", 1);
            createEmployee("Jessica Adams", "Account Manager", new BigDecimal("4200.00"), salesDept, "jessica.adams@workwise.com", 2);

            // Research & Development
            createEmployee("Thomas Wright", "R&D Director", new BigDecimal("5800.00"), researchDept, "thomas.wright@workwise.com", 5);
            createEmployee("Rachel Green", "Research Scientist", new BigDecimal("4900.00"), researchDept, "rachel.green@workwise.com", 3);
            createEmployee("Kevin Martinez", "Product Developer", new BigDecimal("4400.00"), researchDept, "kevin.martinez@workwise.com", 2);
        }
    }

    private Department createDepartment(String name) {
        Department dept = new Department();
        dept.setName(name);
        return departmentRepository.save(dept);
    }

    private void createEmployee(String fullName, String position, BigDecimal salary, 
                              Department department, String email, int yearsOfExperience) {
        // Create employee
        Employee employee = new Employee();
        employee.setFullName(fullName);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setDepartment(department);
        employee.setEmail(email);
        employee.setHireDate(LocalDate.now().minusYears(yearsOfExperience));
        
        // Add some random personal info
        employee.setPhone("+1 " + (random.nextInt(900) + 100) + "-" + (random.nextInt(900) + 100) + "-" + (random.nextInt(9000) + 1000));
        employee.setAddress(random.nextInt(999) + " " + getRandomStreet() + ", " + getRandomCity());

        // Create user account
        String username = email.substring(0, email.indexOf('@'));
        User user = new User();
        user.setUsername(username);
        user.setPassword(username + "123");
        user.setRole(position.toLowerCase().contains("director") || position.toLowerCase().contains("manager") 
            ? User.Role.HR : User.Role.EMPLOYEE);
        user.setActive(true);
        
        // Set up bidirectional relationship
        user.setEmployee(employee);
        employee.setUser(user);
        
        // Save the user which will cascade save the employee
        userRepository.save(user);
    }

    private String getRandomStreet() {
        String[] streets = {"Main Street", "Oak Avenue", "Maple Road", "Cedar Lane", "Pine Street", 
                          "Elm Boulevard", "Washington Street", "Park Avenue", "Lake View Drive"};
        return streets[random.nextInt(streets.length)];
    }

    private String getRandomCity() {
        String[] cities = {"New York", "Los Angeles", "Chicago", "Houston", "Phoenix", 
                         "Philadelphia", "San Antonio", "San Diego", "Dallas"};
        return cities[random.nextInt(cities.length)];
    }
} 