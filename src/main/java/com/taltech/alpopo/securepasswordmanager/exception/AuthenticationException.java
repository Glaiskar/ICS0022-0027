package com.taltech.alpopo.securepasswordmanager.exception;

public class AuthenticationException extends AppException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String format, Object... args) {
        super(format, args);
    }
}
