package com.datascope.app.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 响应封装类
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
     * 成功响应
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return API响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return API响应
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    /**
     * 失败响应
     *
     * @param code    错误码
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 失败响应（默认错误码 500）
     *
     * @param message 错误消息
     * @return API响应
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    /**
     * 设置响应数据并返回当前对象
     *
     * @param data 响应数据
     * @return 当前对象
     */
    public ApiResponse<T> setData(T data) {
        this.data = data;
        return this;
    }
}
