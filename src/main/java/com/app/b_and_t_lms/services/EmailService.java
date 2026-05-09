package com.app.b_and_t_lms.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base.url:http://localhost:3000}")
    private String baseUrl;

    public void sendOtpEmail(String to, String otp, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // Create Thymeleaf context
            Context context = new Context();
            context.setVariable("name", name != null ? name : "User");
            context.setVariable("otp", otp);
            context.setVariable("expiryMinutes", 5);
            context.setVariable("year", java.time.Year.now().getValue());
            context.setVariable("supportEmail", "support@gconnect.co.za");

            // Process HTML template
            String htmlContent = templateEngine.process("otp_email", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("G Connect Consultants OTP");
            helper.setText(htmlContent, true); // true = HTML

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send HTML email: ");
        }
    }

    public void sendPasswordResetConfirmation(String to, String name) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", name != null ? name : "User");
            context.setVariable("year", java.time.Year.now().getValue());
            context.setVariable("loginUrl", baseUrl + "/login");
            context.setVariable("supportEmail", "support@gconnect.co.za");

            String htmlContent = templateEngine.process("reset_confirmation", context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Password Reset Successful");
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            System.err.println("Failed to send confirmation email: ");
        }
    }
}
