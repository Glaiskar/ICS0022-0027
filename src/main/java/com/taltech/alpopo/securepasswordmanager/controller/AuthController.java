package com.taltech.alpopo.securepasswordmanager.controller;

import com.taltech.alpopo.securepasswordmanager.dto.*;
import com.taltech.alpopo.securepasswordmanager.entity.User;
import com.taltech.alpopo.securepasswordmanager.exception.ResourceNotFoundException;
import com.taltech.alpopo.securepasswordmanager.service.EmailService;
import com.taltech.alpopo.securepasswordmanager.service.OtpService;
import com.taltech.alpopo.securepasswordmanager.service.UserService;
import com.taltech.alpopo.securepasswordmanager.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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
    public ResponseEntity<ApiResponse<String>> registerUser(@Valid @RequestBody RegisterRequest request) {
        userService.registerUser(request.getUsername(),
                request.getPassword(), request.getEmail(), request.getMasterPassword());
        ApiResponse<String> response = new ApiResponse<>(true, "User registered successfully!", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest request) {
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

        ApiResponse<String> response = new ApiResponse<>(true, "OTP successfully sent to your email", null);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@RequestBody OtpRequest request,
                                                                         HttpServletResponse response) {
        String username = request.getUsername();
        String otp = request.getOtp();
        boolean isValid = otpService.validateOtp(username, otp);
        if (!isValid) {
            throw new com.taltech.alpopo.securepasswordmanager.exception.AuthenticationException("Incorrect or expired OTP");
        }

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username %s not found", username));

        final String jwt = jwtUtil.generateToken(user);

        Cookie jwtCookie = new Cookie("JWT_TOKEN", jwt);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(7 * 24 * 60 * 60);
        response.addCookie(jwtCookie);

        ApiResponse<String> apiResponse = new ApiResponse<>(true, "Authentication successful", null);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("JWT_TOKEN", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<?> getJwtFromCookie(@CookieValue(name = "JWT_TOKEN", required = false) String jwtToken) {
        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No JWT token found in cookies");
        }
        return ResponseEntity.ok(Collections.singletonMap("token", jwtToken));
    }

    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<String>> getUserInfo(Authentication authentication) {
        return ResponseEntity.ok(new ApiResponse<>(true, "User info retrieved successfully", authentication.getName()));
    }
}
