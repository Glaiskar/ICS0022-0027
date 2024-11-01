package com.taltech.alpopo.securepasswordmanager.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final long OTP_EXPIRATION_SECONDS = 600;
    private final ConcurrentHashMap<String, OtpDetails> otpStorage = new ConcurrentHashMap<>();
    private final SecureRandom secureRandom = new SecureRandom();

    public String generateOtp(String username) {
        String otp = String.format("%06d", secureRandom.nextInt(999999));
        OtpDetails otpDetails = new OtpDetails(otp, Instant.now().plusSeconds(OTP_EXPIRATION_SECONDS));
        otpStorage.put(username, otpDetails);
        return otp;
    }

    public boolean validateOtp(String username, String otp) {
        OtpDetails otpDetails = otpStorage.get(username);
        if (otpDetails == null) {
            return false;
        }

        if (Instant.now().isAfter(otpDetails.getExpiry())) {
            otpStorage.remove(username);
            return false;
        }

        if (otpDetails.getOtp().equals(otp)) {
            otpStorage.remove(username);
            return true;
        }

        return false;
    }

    private static class OtpDetails {
        private final String otp;
        private final Instant expiry;

        public OtpDetails(String otp, Instant expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }

        public String getOtp() {
            return otp;
        }

        public Instant getExpiry() {
            return expiry;
        }
    }
}
