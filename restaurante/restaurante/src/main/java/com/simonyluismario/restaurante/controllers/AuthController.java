package com.simonyluismario.restaurante.controllers;

import com.simonyluismario.restaurante.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    public AuthController(UserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value="error", required=false) String error,
                            @RequestParam(value="logout", required=false) String logout,
                            Model model) {
        model.addAttribute("error", error);
        model.addAttribute("logout", logout);
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() { return "register"; }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String email,
                                 @RequestParam String fullName,
                                 Model model) {
        if (userService.findByUsername(username).isPresent()) {
            model.addAttribute("error", "El usuario ya existe");
            return "register";
        }
        userService.registerWorker(username, password, email, fullName);
        model.addAttribute("msg", "Usuario registrado. Pídele al admin que lo active si es necesario.");
        return "register";
    }

    @GetMapping("/olvide")
    public String olvideForm(){ return "olvide"; }

    @PostMapping("/olvide")
    public String olvideSubmit(@RequestParam String usernameOrEmail, Model model) {
      userService.findByEmail(usernameOrEmail)
        .ifPresent(u -> userService.createPasswordResetToken(u.getEmail()));

    userService.findByUsername(usernameOrEmail)
        .ifPresent(u -> userService.createPasswordResetToken(u.getEmail()));

    model.addAttribute("msg", "Si el usuario/email existe, recibirás un correo con instrucciones.");
    return "olvide";
    }

    @GetMapping("/reset-password")
    public String resetPasswordForm(@RequestParam String token, Model model) {
        boolean ok = userService.validateResetToken(token);
        if (!ok) {
            model.addAttribute("error", "Token inválido o expirado");
            return "olvide";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@RequestParam String token, @RequestParam String password, Model model) {
        boolean ok = userService.resetPassword(token, password);
        if (!ok) {
            model.addAttribute("error", "No se pudo cambiar la contraseña");
            return "reset_password";
        }
        model.addAttribute("msg", "Contraseña cambiada. Ahora puedes ingresar.");
        return "login";
    }

    // After login redirect based on role
    @GetMapping("/after-login")
    public String afterLogin(org.springframework.security.core.Authentication auth) {
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin/menu";
        } else {
            return "redirect:/worker/workspace";
        }
    }
}
