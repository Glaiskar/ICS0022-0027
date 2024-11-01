package com.taltech.alpopo.securepasswordmanager.exception;

public class AppException extends RuntimeException {
    public AppException(String message) {
        super(message);
    }

    public AppException(String format, Object... args) {
        super(String.format(format, args));
    }
}
