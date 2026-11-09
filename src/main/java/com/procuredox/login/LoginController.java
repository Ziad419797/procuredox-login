package com.procuredox.login;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error,
                        Authentication authentication,
                        CsrfToken csrfToken,
                        Model model,
                        @RequestParam(required = false) String registered,
                        @RequestParam(required = false) String otpfailed)  {

        if (authentication != null && authentication.isAuthenticated()) {
            boolean full = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));
            return full ? "redirect:/home" : "redirect:/otp";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
        }

        if (registered != null) {
            model.addAttribute("success", "Account created. Please sign in.");
        }

        if (otpfailed != null) {
            model.addAttribute("error", "Verification failed. Please sign in again.");
        }


        model.addAttribute("csrfToken", csrfToken);
        return "login";


    }



    @GetMapping("/home")
    public String home(CsrfToken csrfToken, Model model) {
        model.addAttribute("csrfToken", csrfToken);
        return "home";
    }


}