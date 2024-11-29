package com.taltech.alpopo.securepasswordmanager.controller;

import com.taltech.alpopo.securepasswordmanager.dto.*;
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

import java.util.ArrayList;
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
    public ResponseEntity<ApiResponse<String>> addCredential(@Valid @RequestBody CredentialRequest request,
                                           Authentication authentication) {
        String username = authentication.getName();
        String masterPassword = request.getMasterPassword();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username %s not found.", username));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            ApiResponse<String> response = new ApiResponse<>(false, "Invalid master password", null);
            return ResponseEntity.status(403).body(response);
        }

        Credential credential = credentialService.addCredential(user,
                request.getServiceName(),
                request.getServiceUsername(),
                request.getPassword(),
                masterPassword);
        ApiResponse<String> response = new ApiResponse<>(true, "Credential created successfully", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/all")
    public ResponseEntity<ApiResponse<List<CredentialDTO>>> getAllCredentials(@RequestBody AllCredentialsRequest request,
                                                                              Authentication authentication) {
        String username = authentication.getName();
        String masterPassword = request.getMasterPassword();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            ApiResponse<List<CredentialDTO>> response = new ApiResponse<>(false, "Invalid master password", new ArrayList<>());
            return ResponseEntity.status(403).body(response);
        }

        List<CredentialDTO> credentials = credentialService.getCredentials(user, masterPassword);
        ApiResponse<List<CredentialDTO>> response = new ApiResponse<>(true, "Credentials retrieved successfully", credentials);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}")
    public ResponseEntity<ApiResponse<CredentialDTO>> getCredentialById(@PathVariable String id,
                                                                        @RequestBody SingleCredentialRequest request,
                                                                        Authentication authentication) {
        String username = authentication.getName();
        String masterPassword = request.getMasterPassword();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            ApiResponse<CredentialDTO> response = new ApiResponse<>(false, "Invalid master password", null);
            return ResponseEntity.status(403).body(response);
        }

        CredentialDTO credential = credentialService.getCredentialDTOById(id, masterPassword)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found."));
        ApiResponse<CredentialDTO> response = new ApiResponse<>(true, "Credential retrieved successfully", credential);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateCredential(@PathVariable String id,
                                              @Valid @RequestBody CredentialRequest request,
                                              Authentication authentication) {
        String username = authentication.getName();
        String masterPassword = request.getMasterPassword();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            ApiResponse<String> response = new ApiResponse<>(false, "Invalid master password", null);
            return ResponseEntity.status(403).body(response);
        }

        Credential credential = credentialService.getCredentialById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found."));

        if (!credential.getUser().getId().equals(user.getId())) {
            ApiResponse<String> response = new ApiResponse<>(false, "Forbidden", null);
            return ResponseEntity.status(403).body(response);
        }

        Credential updatedCredential = credentialService.updateCredential(credential,
                request.getServiceName(),
                request.getServiceUsername(),
                request.getPassword(),
                masterPassword);
        ApiResponse<String> response = new ApiResponse<>(true, "Credential updated successfully", null);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCredential(@PathVariable String id,
                                              @RequestBody SingleCredentialRequest request,
                                              Authentication authentication) {
        String username = authentication.getName();
        String masterPassword = request.getMasterPassword();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!userService.validateMasterPassword(user, masterPassword)) {
            ApiResponse<String> response = new ApiResponse<>(false, "Invalid master password", null);
            return ResponseEntity.status(403).body(response);
        }

        Credential credential = credentialService.getCredentialById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Credential not found."));

        if (!credential.getUser().getId().equals(user.getId())) {
            ApiResponse<String> response = new ApiResponse<>(false, "Forbidden", null);
            return ResponseEntity.status(403).body(response);
        }

        credentialService.deleteCredential(id);
        ApiResponse<String> response = new ApiResponse<>(true, "Credential deleted successfully", null);
        return ResponseEntity.ok(response);
    }
}
