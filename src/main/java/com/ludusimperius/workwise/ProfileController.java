package com.ludusimperius.workwise;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            System.out.println("ViewProfile - Session user: " + user.getUsername());
            if (user.getEmployee() != null) {
                System.out.println("ViewProfile - Session employee: " + user.getEmployee());
            }
            
            // Always get fresh data from database
            User freshUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("ViewProfile - Fresh user from DB: " + freshUser.getUsername());
            
            // Ensure employee data is loaded with a separate query to avoid lazy loading issues
            if (freshUser.getEmployee() != null) {
                Employee freshEmployee = employeeRepository.findById(freshUser.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
                System.out.println("ViewProfile - Fresh employee from DB: " + freshEmployee);
                
                freshUser.setEmployee(freshEmployee);
                
                // Update session with fresh data
                session.setAttribute("user", freshUser);
            }
            
            model.addAttribute("user", freshUser);
            return "profile/view";
        } catch (Exception e) {
            System.err.println("ViewProfile - Error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/edit")
    public String showEditForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        try {
            // Refresh user data from database
            user = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Ensure employee data is loaded
            if (user.getEmployee() != null) {
                Employee employee = employeeRepository.findById(user.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
                user.setEmployee(employee);
            }
            
            model.addAttribute("user", user);
            return "profile/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading profile: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") User updatedUser, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            System.out.println("Updating profile for user: " + currentUser.getUsername());
            System.out.println("Updated data received: " + updatedUser.getEmployee());
            
            // Get fresh data from database
            User existingUser = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            System.out.println("Existing user from DB: " + existingUser.getUsername());
            System.out.println("Existing employee: " + existingUser.getEmployee());
            
            // Update only allowed fields
            if (updatedUser.getEmployee() != null && existingUser.getEmployee() != null) {
                Employee existingEmployee = employeeRepository.findById(existingUser.getEmployee().getId())
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
                Employee updatedEmployee = updatedUser.getEmployee();
                
                System.out.println("Existing employee from DB: " + existingEmployee);
                System.out.println("Updated employee data: " + updatedEmployee);
                
                // Update only non-null and non-empty fields
                String newFullName = updatedEmployee.getFullName();
                if (newFullName != null && !newFullName.trim().isEmpty()) {
                    System.out.println("Updating full name to: " + newFullName.trim());
                    existingEmployee.setFullName(newFullName.trim());
                }
                
                String newEmail = updatedEmployee.getEmail();
                if (newEmail != null && !newEmail.trim().isEmpty()) {
                    existingEmployee.setEmail(newEmail.trim());
                }
                
                String newPhone = updatedEmployee.getPhone();
                if (newPhone != null) {
                    existingEmployee.setPhone(newPhone.trim());
                }
                
                existingEmployee.setBirthDate(updatedEmployee.getBirthDate());
                
                String newAddress = updatedEmployee.getAddress();
                if (newAddress != null) {
                    existingEmployee.setAddress(newAddress.trim());
                }
                
                String newLinkedin = updatedEmployee.getLinkedin();
                if (newLinkedin != null) {
                    existingEmployee.setLinkedin(newLinkedin.trim());
                }
                
                String newBio = updatedEmployee.getBio();
                if (newBio != null) {
                    existingEmployee.setBio(newBio.trim());
                }
                
                // Save the updated employee first
                existingEmployee = employeeRepository.save(existingEmployee);
                System.out.println("Saved employee: " + existingEmployee);
                
                // Update the user with the saved employee
                existingUser.setEmployee(existingEmployee);
                
                // Save the updated user
                existingUser = userRepository.save(existingUser);
                System.out.println("Saved user: " + existingUser);
                
                // Update session with fresh data
                session.setAttribute("user", existingUser);
            }
            
            return "redirect:/profile";
        } catch (Exception e) {
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error updating profile: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        return "profile/change-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                               @RequestParam String newPassword,
                               @RequestParam String confirmPassword,
                               HttpSession session,
                               Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get fresh user data from database
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if current password matches
        if (!currentPassword.equals(existingUser.getPassword())) {
            model.addAttribute("error", "Current password is incorrect");
            return "profile/change-password";
        }
        
        // Validate new password
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New passwords do not match");
            return "profile/change-password";
        }
        
        if (newPassword.length() < 6) {
            model.addAttribute("error", "New password must be at least 6 characters long");
            return "profile/change-password";
        }
        
        // Update password
        existingUser.setPassword(newPassword);
        existingUser.setTemporaryPassword(false);
        userRepository.save(existingUser);
        
        // Update session
        session.setAttribute("user", existingUser);
        
        return "redirect:/profile?passwordChanged=true";
    }
} 