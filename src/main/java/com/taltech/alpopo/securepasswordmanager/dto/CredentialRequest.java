package com.taltech.alpopo.securepasswordmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CredentialRequest {
    @NotBlank
    private String serviceName;
    @NotBlank
    private String serviceUsername;
    @NotBlank
    private String password;
}
