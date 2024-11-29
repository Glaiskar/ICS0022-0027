package com.taltech.alpopo.securepasswordmanager.service;

import com.taltech.alpopo.securepasswordmanager.dto.CredentialDTO;
import com.taltech.alpopo.securepasswordmanager.entity.Credential;
import com.taltech.alpopo.securepasswordmanager.entity.User;
import com.taltech.alpopo.securepasswordmanager.exception.ResourceNotFoundException;
import com.taltech.alpopo.securepasswordmanager.repository.CredentialRepository;
import com.taltech.alpopo.securepasswordmanager.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final EncryptionUtil encryptionUtil;

    @Autowired
    public CredentialService (CredentialRepository credentialRepository, EncryptionUtil encryptionUtil) {
        this.credentialRepository = credentialRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public Credential addCredential(User user, String serviceName, String serviceUsername, String plainPassword, String masterPassword) {
        String encryptedPassword = encryptionUtil.encrypt(plainPassword, masterPassword);
        Credential credential = Credential.builder()
                .serviceName(serviceName)
                .serviceUsername(serviceUsername)
                .encryptedPassword(encryptedPassword)
                .user(user)
                .build();
        return credentialRepository.save(credential);
    }

    public List<CredentialDTO> getCredentials(User user, String masterPassword) {
        List<Credential> credentials = credentialRepository.findByUser(user);
        return credentials.stream()
                .map(credential -> CredentialDTO.builder()
                        .id(credential.getId())
                        .serviceName(credential.getServiceName())
                        .serviceUsername(credential.getServiceUsername())
                        .decryptedPassword(encryptionUtil.decrypt(credential.getEncryptedPassword(), masterPassword))
                        .build())
                .collect(Collectors.toList());
    }

    public Optional<Credential> getCredentialById(String id) {
        Credential credential = credentialRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));
        return Optional.ofNullable(credential);
    }

    public Optional<CredentialDTO> getCredentialDTOById(String id, String masterPassword) {
        Credential credential = credentialRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found with id: " + id));
        return Optional.ofNullable(CredentialDTO.builder()
                .id(credential.getId())
                .serviceName(credential.getServiceName())
                .serviceUsername(credential.getServiceUsername())
                .decryptedPassword(encryptionUtil.decrypt(credential.getEncryptedPassword(), masterPassword))
                .build());
    }

    public Credential updateCredential(Credential credential,
                                          String serviceName,
                                          String serviceUsername,
                                          String plainPassword,
                                          String masterPassword) {
        credential.setServiceName(serviceName);
        credential.setServiceUsername(serviceUsername);
        String encryptedPassword = encryptionUtil.encrypt(plainPassword, masterPassword);
        credential.setEncryptedPassword(encryptedPassword);
        return credentialRepository.save(credential);
    }

    public void deleteCredential(String id) {
        credentialRepository.deleteById(UUID.fromString(id));
    }
}
