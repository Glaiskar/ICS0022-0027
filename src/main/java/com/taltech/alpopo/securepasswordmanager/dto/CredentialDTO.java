package com.taltech.alpopo.securepasswordmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialDTO {
    private Long id;
    private String serviceName;
    private String serviceUsername;
    private String decryptedPassword;
}
