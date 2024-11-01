package com.taltech.alpopo.securepasswordmanager.exception;

public class DuplicateResourceException extends AppException {
    public DuplicateResourceException(String message, String username) {
        super(message);
    }

    public DuplicateResourceException(String format, Object... args) {
        super(format, args);
    }
}
