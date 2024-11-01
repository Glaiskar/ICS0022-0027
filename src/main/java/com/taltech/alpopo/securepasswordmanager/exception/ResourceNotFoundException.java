package com.taltech.alpopo.securepasswordmanager.exception;

public class ResourceNotFoundException extends AppException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String format, Object... args) {
        super(format, args);
    }
}
