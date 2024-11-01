package com.taltech.alpopo.securepasswordmanager.controller;

import com.taltech.alpopo.securepasswordmanager.dto.AuthenticationRequest;
import com.taltech.alpopo.securepasswordmanager.dto.AuthenticationResponse;
import com.taltech.alpopo.securepasswordmanager.dto.RegisterRequest;
import com.taltech.alpopo.securepasswordmanager.entity.User;
import com.taltech.alpopo.securepasswordmanager.exception.ResourceNotFoundException;
import com.taltech.alpopo.securepasswordmanager.service.EmailService;
import com.taltech.alpopo.securepasswordmanager.service.OtpService;
import com.taltech.alpopo.securepasswordmanager.service.UserService;
import com.taltech.alpopo.securepasswordmanager.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailService emailService;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil,
                          OtpService otpService,
                          EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request.getUsername(),
                request.getPassword(), request.getEmail(), request.getMasterPassword());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new com.taltech.alpopo.securepasswordmanager.exception.AuthenticationException("Incorrect username or password");
        }

        String otp = otpService.generateOtp(request.getUsername());

        User user = userService.findByUsername(request.getUsername()).orElseThrow(
                () -> new ResourceNotFoundException("User with username %s not found", request.getUsername())
        );
        String userEmail = user.getEmail();

        emailService.sendOtpEmail(userEmail, otp);

        return ResponseEntity.ok("OTP successfully sent to your email");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String username,
                                       @RequestParam String otp) {

        boolean isValid = otpService.validateOtp(username, otp);
        if(!isValid) {
            throw new com.taltech.alpopo.securepasswordmanager.exception.AuthenticationException("Incorrect or expired OTP");
        }

        User user = userService.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User with username %s not found", username)
        );

        final String jwt = jwtUtil.generateToken(user);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
