package com.lebanonplatform.common.response;

import java.util.List;

public record ErrorResponse(
        boolean success,
        String message,
        String code,
        List<String> errors
) {
    public static ErrorResponse of(String message, String code) {
        return new ErrorResponse(false, message, code, List.of());
    }

    public static ErrorResponse of(String message, String code, List<String> errors) {
        return new ErrorResponse(false, message, code, errors);
    }
}
