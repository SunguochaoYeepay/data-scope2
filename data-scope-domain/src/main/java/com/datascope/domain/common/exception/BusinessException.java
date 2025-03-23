package com.datascope.domain.common.exception;

import lombok.Getter;

/**
 * 业务异常基类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误消息
     */
    private final String message;

    /**
     * 错误数据
     */
    private final Object data;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public BusinessException(String code, String message) {
        this(code, message, null);
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     */
    public BusinessException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.data = null;
    }

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     * @param cause   原始异常
     */
    public BusinessException(String code, String message, Object data, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @return 业务异常
     */
    public static BusinessException of(String code, String message) {
        return new BusinessException(code, message);
    }

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     * @return 业务异常
     */
    public static BusinessException of(String code, String message, Object data) {
        return new BusinessException(code, message, data);
    }

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @param cause   原始异常
     * @return 业务异常
     */
    public static BusinessException of(String code, String message, Throwable cause) {
        return new BusinessException(code, message, cause);
    }

    /**
     * 创建业务异常
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     * @param cause   原始异常
     * @return 业务异常
     */
    public static BusinessException of(String code, String message, Object data, Throwable cause) {
        return new BusinessException(code, message, data, cause);
    }
}