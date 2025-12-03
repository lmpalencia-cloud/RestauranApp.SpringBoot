package com.simonyluismario.restaurante.services;

import com.simonyluismario.restaurante.models.*;
import com.simonyluismario.restaurante.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    
    private final Map<String, String> resetTokens = new HashMap<>();

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User createAdminIfNotExists(String username, String rawPassword, String email, String fullName) {
        Optional<User> u = userRepository.findByUsername(username);
        if (u.isPresent()) return u.get();

        User admin = new User();
        admin.setUsername(username);
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setEmail(email);
        admin.setFullName(fullName);
        admin.setRole(Role.ADMIN);
        admin.setEnabled(true);
        return userRepository.save(admin);
    }

    public User registerWorker(String username, String rawPassword, String email, String fullName) {
        User w = new User();
        w.setUsername(username);
        w.setPassword(passwordEncoder.encode(rawPassword));
        w.setEmail(email);
        w.setFullName(fullName);
        w.setRole(Role.WORKER);
        w.setEnabled(true);
        return userRepository.save(w);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void createPasswordResetToken(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return;
        String token = UUID.randomUUID().toString();
        resetTokens.put(token, userOpt.get().getEmail());

        String link = "http://localhost:8080/reset-password?token=" + token;
        String subject = "Restablecer contraseña - Restaurant";
        String body = "Hola " + userOpt.get().getFullName() + ",\n\nHaz click en este enlace para restablecer tu contraseña:\n" + link + "\n\nSi no fuiste tú, ignora este correo.";
        emailService.sendSimpleMessage(userOpt.get().getEmail(), subject, body);
    }

    public boolean validateResetToken(String token) {
        return resetTokens.containsKey(token);
    }

    public boolean resetPassword(String token, String newPassword) {
        String email = resetTokens.get(token);
        if (email == null) return false;
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User u = userOpt.get();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        resetTokens.remove(token);
        return true;
    }
    public List<User> findAllUsers() {
    return userRepository.findAll();
}
public boolean deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
        return false;
    }

    userRepository.deleteById(id);
    return true;
}


}


