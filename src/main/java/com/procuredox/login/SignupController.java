package com.procuredox.login;

import com.procuredox.login.user.SignupForm;
import com.procuredox.login.user.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupPage(Authentication authentication, CsrfToken csrfToken, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }
        model.addAttribute("form", new SignupForm("", "", "", "", "", ""));
        model.addAttribute("errors", new HashMap<String, String>());
        model.addAttribute("csrfToken", csrfToken);
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("form") SignupForm form,
                         BindingResult result,
                         CsrfToken csrfToken,
                         Model model) {

        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));

        if (!errors.containsKey("password") && !errors.containsKey("confirmPassword")
                && !form.password().equals(form.confirmPassword())) {
            errors.put("confirmPassword", "Passwords do not match");
        }
        if (!errors.containsKey("email") && userService.emailTaken(form.email())) {
            errors.put("email", "This email is already registered");
        }
        if (!errors.containsKey("phone") && userService.phoneTaken(form.phone())) {
            errors.put("phone", "This phone number is already registered");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            model.addAttribute("csrfToken", csrfToken);
            return "signup";
        }

        userService.register(form);
        return "redirect:/login?registered";
    }
}