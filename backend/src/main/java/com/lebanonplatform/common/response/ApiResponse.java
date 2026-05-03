package com.lebanonplatform.common.response;

import java.util.List;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        String code,
        List<String> errors
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, null, List.of());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, List.of());
    }

    public static <T> ApiResponse<T> failure(String message, String code, List<String> errors) {
        return new ApiResponse<>(false, message, null, code, errors);
    }
}
