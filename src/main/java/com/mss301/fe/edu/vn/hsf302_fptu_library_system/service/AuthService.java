package com.mss301.fe.edu.vn.hsf302_fptu_library_system.service;

public interface AuthService {
    void resetPassword(String email);
    boolean changePassword(String email, String oldPassword, String newPassword);
}
