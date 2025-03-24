package com.datascope.app.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标准API响应格式
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * 响应码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, true);
    }

    /**
     * 创建成功响应（带自定义消息）
     *
     * @param message 成功消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, true);
    }

    /**
     * 创建错误响应
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null, false);
    }

    /**
     * 创建错误响应（带自定义错误码）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    /**
     * 创建错误响应（带数据）
     *
     * @param message 错误消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(500, message, data, false);
    }

    /**
     * 创建错误响应（带自定义错误码和数据）
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return API响应对象
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, false);
    }
}
