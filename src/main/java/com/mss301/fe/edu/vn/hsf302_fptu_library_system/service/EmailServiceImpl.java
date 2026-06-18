package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailServiceImpl implements EmailService {
    JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    @NonFinal
    String fromEmailAddress;
    SpringTemplateEngine templateEngine;

    @Async
    public void sendNewPasswordEmail(String to, String fullName, String newPassword) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("password", newPassword);
            String html = templateEngine.process("email/new-password", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmailAddress);
            helper.setTo(to);
            helper.setSubject("FPTU Library - Mật khẩu mới");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không gửi được email", e);
        }
    }
}
