package com.taltech.alpopo.securepasswordmanager.exception;

public class InvalidMasterPasswordException extends AppException {
    public InvalidMasterPasswordException(String message) {
        super(message);
    }

    public InvalidMasterPasswordException(String format, Object... args) {
        super(format, args);
    }
}
