package com.datascope.app.common;

import lombok.Data;

/**
 * 统一API响应结果
 *
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

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
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应
     *
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    /**
     * 失败响应
     *
     * @param code 错误码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    /**
     * 参数错误响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 参数错误响应结果
     */
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * 未授权响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 未授权响应结果
     */
    public static <T> Result<T> unauthorized(String message) {
        return error(401, message);
    }

    /**
     * 禁止访问响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 禁止访问响应结果
     */
    public static <T> Result<T> forbidden(String message) {
        return error(403, message);
    }

    /**
     * 资源不存在响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 资源不存在响应结果
     */
    public static <T> Result<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * 服务器错误响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 服务器错误响应结果
     */
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }
}