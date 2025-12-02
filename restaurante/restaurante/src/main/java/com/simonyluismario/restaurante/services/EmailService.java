package com.simonyluismario.restaurante.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


@Service
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;
    public EmailService(JavaMailSender mailSender){ this.mailSender = mailSender; }

     public void sendSimpleMessage(String to, String subject, String text) {
    try {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    } catch (Exception ex) {
        System.out.println("Error enviando correo: " + ex.getMessage());
    }
}
}
