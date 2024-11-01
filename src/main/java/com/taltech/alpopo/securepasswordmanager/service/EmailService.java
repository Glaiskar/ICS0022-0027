package com.taltech.alpopo.securepasswordmanager.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}
