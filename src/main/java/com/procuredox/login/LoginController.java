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
                        Model model) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/home";
        }

        if (error != null) {
            model.addAttribute("error", "Invalid email or password");
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