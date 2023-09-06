package com.alibou.security.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

//    public void sendEmail(String to, int otpCode) {
//
//        String emailText = "Dear User,\n\n";
//        emailText += "Thank you for using our secure service. To verify your identity, please use the following OTP code:\n\n";
//        emailText += "OTP Code: " + otpCode + "\n\n";
//        emailText += "This OTP is valid for a single use and will expire shortly. Please do not share it with anyone.\n\n";
//        emailText += "If you did not request this code, please ignore this email.\n\n";
//        emailText += "Best regards,\n";
//        emailText += "E-COMMERCE WEB APPLICATION Support Team";
//
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject("Your One-Time Password (OTP) Verification Code");
//        message.setText(emailText);
//        javaMailSender.send(message);
//    }

    public void sendEmail(String to, int otpCode) {
        String emailText = "<html><body>";
        emailText += "<div style=\"font-family: Arial, sans-serif;\">";
        emailText += "<h1 style=\"color:#9004bf;font-size:2rem\">E-COMMERCE WEB APPLICATION</h1>";
        emailText += "<p style=\"color:black;font-weight:bold;font-size:1.6rem;margin-bottom:1.2rem;\">Thank you for using our secure service. To verify your identity, please use the following OTP code:</p>";
        emailText += "<p style=\"font-weight: bold; font-size: 18px;margin-bottom:0.3rem;\">OTP Code: " + otpCode + "</p>";
        emailText += "</div></body></html>";



        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Your One-Time Password (OTP) Verification Code");
            helper.setText(emailText, true); // Set as HTML
            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Handle the exception
        }
    }



}
