package com.training.thymeleafmid.user;

import com.training.thymeleafmid.LoginRequest;
import com.training.thymeleafmid.config.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping("/")
@Controller
public class UserController {
    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("details", new LoginRequest());
        return "login";
    }
    @PostMapping("/login")
    public String processLogin(
            @ModelAttribute LoginRequest loginRequest // Use @ModelAttribute to get the form data
            , HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ){
        try {
            Authentication authentication =  authService.authenticateAndSetCookie(response, loginRequest);
            if(authentication.getAuthorities().stream().anyMatch(
                    grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_STUDENT"))) {
                return "redirect:/student/view";
            }
            else {
                return "redirect:/teacher/view";
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid credentials. Please try again.");
            return "redirect:/login?error=true";
        }
    }
}
