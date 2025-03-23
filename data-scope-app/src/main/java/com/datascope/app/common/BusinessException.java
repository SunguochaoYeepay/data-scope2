package com.datascope.app.common;

import lombok.Getter;

/**
 * Business exception
 * 
 * @author dreambt
 */
@Getter
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Error code
     */
    private final String code;

    /**
     * Create business exception with code and message
     *
     * @param code Error code
     * @param message Error message
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Create business exception with message
     *
     * @param message Error message
     */
    public BusinessException(String message) {
        this("500", message);
    }

    /**
     * Create business exception with cause
     *
     * @param message Error message
     * @param cause Cause
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = "500";
    }

    /**
     * Create business exception with code, message and cause
     *
     * @param code Error code
     * @param message Error message
     * @param cause Cause
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    /**
     * Get error code
     *
     * @return Error code
     */
    public String getCode() {
        return code;
    }
}