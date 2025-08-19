package com.crudapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@crudapp.com}")
    private String fromEmail;

    public void sendOtpEmail(String toEmail, String otpCode, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔐 Your OTP for CRUD App Login");
            
            String emailBody = String.format(
                "Hello %s,\n\n" +
                "You have successfully signed in with Google to CRUD App.\n\n" +
                "To complete your login, please enter this OTP code:\n\n" +
                "🔑 OTP CODE: %s\n\n" +
                "⏰ This code will expire in 5 minutes.\n\n" +
                "If you didn't request this login, please ignore this email.\n\n" +
                "Best regards,\n" +
                "CRUD App Team",
                userName, otpCode
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🎉 Welcome to CRUD App!");
            
            String emailBody = String.format(
                "Hello %s,\n\n" +
                "Welcome to CRUD App! Your account has been successfully verified.\n\n" +
                "You can now access all features of the application.\n\n" +
                "Thank you for choosing CRUD App!\n\n" +
                "Best regards,\n" +
                "CRUD App Team",
                userName
            );
            
            message.setText(emailBody);
            mailSender.send(message);
            
        } catch (Exception e) {
            // Don't throw exception for welcome email failure
            System.err.println("Failed to send welcome email: " + e.getMessage());
        }
    }
}
