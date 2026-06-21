package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

public interface EmailService {
    void sendNewPasswordEmail(String to, String fullName, String newPassword);
    void sendWaitingBookNotification(String email, String fullName, String bookTitle);
    void sendHoldExpiredEmail(String to, String fullName, String bookTitle);
}
