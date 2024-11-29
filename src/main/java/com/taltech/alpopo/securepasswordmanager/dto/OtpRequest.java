package com.taltech.alpopo.securepasswordmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {
    @NotBlank(message = "OTP is required")
    private String otp;
    @NotBlank(message = "Username is required")
    private String username;
}
