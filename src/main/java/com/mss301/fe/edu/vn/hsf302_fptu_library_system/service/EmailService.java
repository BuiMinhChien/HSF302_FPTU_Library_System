package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

public interface EmailService {
    void sendNewPasswordEmail(String to, String fullName, String newPassword);
}
