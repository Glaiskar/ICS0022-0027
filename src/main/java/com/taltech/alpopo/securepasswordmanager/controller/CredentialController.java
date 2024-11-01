package com.taltech.alpopo.securepasswordmanager.controller;

import com.taltech.alpopo.securepasswordmanager.dto.CredentialDTO;
import com.taltech.alpopo.securepasswordmanager.dto.CredentialRequest;
import com.taltech.alpopo.securepasswordmanager.entity.Credential;
import com.taltech.alpopo.securepasswordmanager.entity.User;
import com.taltech.alpopo.securepasswordmanager.exception.ResourceNotFoundException;
import com.taltech.alpopo.securepasswordmanager.service.CredentialService;
import com.taltech.alpopo.securepasswordmanager.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credentials")
public class CredentialController {

    private final CredentialService credentialService;
    private final UserService userService;

    @Autowired
    public CredentialController(CredentialService credentialService, UserService userService) {
        this.credentialService = credentialService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> addCredential(@Valid @RequestBody CredentialRequest request,
                                           @RequestParam String masterPassword,
                                           Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username %s not found.", username));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            return ResponseEntity.status(403).body("Invalid master password");
        }

        Credential credential = credentialService.addCredential(user,
                request.getServiceName(),
                request.getServiceUsername(),
                request.getPassword(),
                masterPassword);
        return ResponseEntity.ok("Credential created successfully");
    }

    @GetMapping
    public ResponseEntity<List<CredentialDTO>> getAllCredentials(@RequestParam String masterPassword,
                                                                 Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
           return ResponseEntity.status(403).build();
        }

        List<CredentialDTO> credentials = credentialService.getCredentials(user, masterPassword);
        return ResponseEntity.ok(credentials);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCredential(@PathVariable Long id,
                                              @Valid @RequestBody CredentialRequest request,
                                              @RequestParam String masterPassword,
                                              Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            return ResponseEntity.status(403).body("Invalid master password.");
        }

        Credential credential = credentialService.getCredentialById(id, masterPassword)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found."));

        if (!credential.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        Credential updatedCredential = credentialService.updateCredential(credential,
                request.getServiceName(),
                request.getServiceUsername(),
                request.getPassword(),
                masterPassword);
        return ResponseEntity.ok("Credential updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCredential(@PathVariable Long id,
                                              @RequestParam String masterPassword,
                                              Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            return ResponseEntity.status(403).body("Invalid master password.");
        }

        Credential credential = credentialService.getCredentialById(id, masterPassword)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found."));

        if (!credential.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }

        credentialService.deleteCredential(id);
        return ResponseEntity.ok("Credential deleted successfully.");
    }
}
