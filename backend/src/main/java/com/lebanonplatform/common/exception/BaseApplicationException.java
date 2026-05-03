package com.lebanonplatform.common.exception;

public class BaseApplicationException extends RuntimeException {

    private final String code;

    public BaseApplicationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
