package io.mountblue.BlogApplication.controller;

import io.mountblue.BlogApplication.dao.ServiceImplementation;
import io.mountblue.BlogApplication.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    private ServiceImplementation serviceImplementation;
    private PasswordEncoder passwordEncoder;

    public AuthenticationController(ServiceImplementation serviceImplementation, PasswordEncoder passwordEncoder) {
        this.serviceImplementation = serviceImplementation;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showMyLoginPage() {
        return "login-page";
    }

    @GetMapping("/signup")
    public String showSignUpForm(Model model) {
        model.addAttribute("user", new User());
        return "signup-page";
    }

    @PostMapping("/signup")
    public String processSignUpForm(User user, RedirectAttributes attributes) {
        if(serviceImplementation.userExists(user.getEmail())) {
            attributes.addFlashAttribute("error", "Email is already taken. Please choose a different email.");
            return "redirect:/signup";
        }
        if(serviceImplementation.usernameExist(user.getUsername())) {
            attributes.addFlashAttribute("error", "Username is already taken. Please choose a different username.");

        }
        user.setRole("User");
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        serviceImplementation.saveUser(user);

        return "redirect:/login?signupSuccess";
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "access-denied";
    }
}
