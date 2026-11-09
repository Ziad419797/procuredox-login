package com.procuredox.login.otp;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class OtpController {

    private final OtpService otpService;
    private final SecurityContextRepository securityContextRepository =
            new HttpSessionSecurityContextRepository();

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @GetMapping("/otp")
    public String otpPage(CsrfToken csrfToken, Model model) {
        model.addAttribute("csrfToken", csrfToken);
        return "otp";
    }

    @PostMapping("/otp")
    public String verify(@RequestParam String code,
                         Authentication authentication,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         CsrfToken csrfToken,
                         Model model) {

        OtpResult result = otpService.verify(authentication.getName(), code);

        if (result == OtpResult.OK) {
            request.changeSessionId();
            var fullAuth = UsernamePasswordAuthenticationToken.authenticated(
                    authentication.getPrincipal(), null,
                    List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(fullAuth);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return "redirect:/home";
        }

        if (result == OtpResult.WRONG) {
            model.addAttribute("csrfToken", csrfToken);
            model.addAttribute("error", "Wrong code. Try again.");
            return "otp";
        }

        // EXPIRED / TOO_MANY_ATTEMPTS / NO_CODE: start over
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/login?otpfailed";
    }
}