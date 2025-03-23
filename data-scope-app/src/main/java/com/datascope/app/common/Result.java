package com.datascope.app.common;

import lombok.Data;

/**
 * Standard API response wrapper
 * 
 * @author dreambt
 */
@Data
public class Result<T> {
    /**
     * Response code
     */
    private String code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    private Result() {
    }

    private Result(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * Create success response with data
     *
     * @param data Response data
     * @return Success response
     */
    public static <T> Result<T> success(T data) {
        return new Result<>("200", "Success", data);
    }

    /**
     * Create success response without data
     *
     * @return Success response
     */
    public static <T> Result<T> success() {
        return new Result<>("200", "Success", null);
    }

    /**
     * Create error response
     *
     * @param code Error code
     * @param message Error message
     * @return Error response
     */
    public static <T> Result<T> error(String code, String message) {
        return new Result<>(code, message, null);
    }

    /**
     * Create error response with default code
     *
     * @param message Error message
     * @return Error response
     */
    public static <T> Result<T> error(String message) {
        return error("500", message);
    }

    /**
     * Check if response is successful
     *
     * @return True if successful
     */
    public boolean isSuccess() {
        return "200".equals(code);
    }
}