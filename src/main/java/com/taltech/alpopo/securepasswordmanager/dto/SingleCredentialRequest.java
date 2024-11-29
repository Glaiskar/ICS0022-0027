package com.taltech.alpopo.securepasswordmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SingleCredentialRequest {
    @NotBlank(message = "Master password is required")
    private String masterPassword;
}
