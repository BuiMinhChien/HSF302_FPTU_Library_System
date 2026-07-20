package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

import java.time.LocalDate;

public interface EmailService {
    void sendNewPasswordEmail(String to, String fullName, String newPassword);
    void sendWaitingBookNotification(String email, String fullName, String bookTitle);
    void sendHoldExpiredEmail(String to, String fullName, String bookTitle);
    void sendReturnReminderEmail(String to, String fullName, String bookTitle, LocalDate dueDate);
}
