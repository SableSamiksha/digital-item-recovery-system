package com.itemrecovery.controller;

import com.itemrecovery.dto.LoginRequest;
import com.itemrecovery.dto.RegisterRequest;
import com.itemrecovery.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for authentication operations.
 * Handles user registration and login pages.
 */
@Controller
public class AuthController {
    
    @Autowired
    private UserService userService;

    /**
     * Display login page.
     * @param model the model
     * @param error error flag from query parameter
     * @param logout logout flag from query parameter
     * @return login page template name
     */
    @GetMapping("/login")
    public String loginPage(Model model, String error, String logout) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully");
        }
        return "login";
    }

    /**
     * Display registration page.
     * @param model the model
     * @return register page template name
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    /**
     * Handle user registration.
     * @param registerRequest the registration request
     * @param bindingResult validation results
     * @param redirectAttributes redirect attributes
     * @return redirect to login page on success, register page on error
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(registerRequest);
            redirectAttributes.addFlashAttribute("message", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            bindingResult.rejectValue("username", "error.registerRequest", e.getMessage());
            return "register";
        }
    }

    /**
     * Root mapping - redirect to login or dashboard.
     * @return redirect to dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
