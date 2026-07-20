package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    @NonFinal
    private String fromEmailAddress;

    @Value("${borrow.request.expire-days}")
    private int expireDays;

    @Value("${borrow.history.reminder}")
    private int reminder;

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

    @Async
    public void sendWaitingBookNotification(
            String to,
            String fullName,
            String bookTitle
    ) {
        try {
            LocalDate deadline = calculatePickupDeadline(LocalDate.now());
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("bookTitle", bookTitle);
            context.setVariable("expireDays", expireDays);
            context.setVariable("deadline", deadline.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            String html = templateEngine.process("email/book-ready-notification", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmailAddress);
            helper.setTo(to);
            helper.setSubject("FPTU Library - Sách đã sẵn sàng để nhận");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không gửi được email", e);
        }
    }

    private LocalDate calculatePickupDeadline(LocalDate startDate) {
        int workingDays = 0;
        LocalDate deadline = startDate;
        while (workingDays < expireDays) {
            deadline = deadline.plusDays(1);
            if (deadline.getDayOfWeek() != DayOfWeek.SATURDAY
                    && deadline.getDayOfWeek() != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        return deadline;
    }

    @Async
    public void sendHoldExpiredEmail(String to,
                                     String fullName,
                                     String bookTitle) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("bookTitle", bookTitle);
            String html = templateEngine.process("email/hold-book-expired", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmailAddress);
            helper.setTo(to);
            helper.setSubject("FPTU Library - Thông báo hết hạn giữ sách");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không gửi được email", e);
        }
    }

    @Async
    public void sendReturnReminderEmail(
            String to,
            String fullName,
            String bookTitle,
            LocalDate dueDate
    ) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("bookTitle", bookTitle);
            context.setVariable("dueDate", dueDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            String html = templateEngine.process("email/return-reminder-notification", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmailAddress);
            helper.setTo(to);
            helper.setSubject("FPTU Library - Nhắc nhở hạn trả sách");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Không gửi được email", e);
        }
    }
}
