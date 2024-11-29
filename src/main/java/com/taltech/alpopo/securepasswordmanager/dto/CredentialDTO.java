package com.taltech.alpopo.securepasswordmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialDTO {
    private UUID id;
    private String serviceName;
    private String serviceUsername;
    private String decryptedPassword;
}
