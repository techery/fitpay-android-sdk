package com.fitpay.android.utils;

/**
 * Indicates that an error occurred while validating the identity
 */
public class ValidationException extends Exception {
    public ValidationException() {
        super();
    }

    public ValidationException(String s) {
        super(s);
    }

    private static final long serialVersionUID = 1L;
}