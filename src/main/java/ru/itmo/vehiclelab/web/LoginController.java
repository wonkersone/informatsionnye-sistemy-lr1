package ru.itmo.vehiclelab.web;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.dao.DataIntegrityViolationException;
import jakarta.validation.Valid;
import ru.itmo.vehiclelab.service.UserService;
@Controller
public class LoginController {
    private final UserService users;
    public LoginController(UserService users) { this.users = users; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registration(Model model) {
        model.addAttribute("registrationForm", new RegistrationForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegistrationForm registrationForm, BindingResult errors) {
        if (errors.hasErrors()) return "register";
        try {
            users.register(registrationForm.getUsername(), registrationForm.getPassword());
        } catch (IllegalArgumentException ex) {
            errors.rejectValue("username", "registration", ex.getMessage());
            return "register";
        } catch (DataIntegrityViolationException ex) {
            errors.rejectValue("username", "duplicate", "Этот логин уже занят");
            return "register";
        }
        return "redirect:/login?registered";
    }
}
