package com.expensetracker.expensetracker.controllers;

import com.expensetracker.expensetracker.models.User;
import com.expensetracker.expensetracker.services.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
// Make sure you are importing the PasswordEncoder interface or the BCryptPasswordEncoder class
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // --- FIX STEP 1: Inject the PasswordEncoder bean ---
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String showLoginPage() {
        return "layouts/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "layouts/register";
    }

    @PostMapping("/process_register")
    public String processRegistration(@Valid User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // If validation fails (e.g., empty email), return to the form
            model.addAttribute("user", user); // Pass the user back to preserve entered data
            return "layouts/register";
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            model.addAttribute("errorMessage", "Email already exists!");
            model.addAttribute("user", user); // Pass the user back
            return "layouts/register";
        }

        // --- FIX STEP 2: Use the injected encoder ---
        // Do NOT create a new instance like `new BCryptPasswordEncoder()`
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);

        return "redirect:/login?register_success";
    }
}