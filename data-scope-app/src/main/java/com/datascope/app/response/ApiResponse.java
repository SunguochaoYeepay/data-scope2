package com.datascope.app.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 响应封装类
 *
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API响应")
public class ApiResponse<T> {

    /**
     * 状态码
     */
    @Schema(description = "状态码", example = "200")
    private int code;

    /**
     * 消息
     */
    @Schema(description = "消息", example = "操作成功")
    private String message;

    /**
     * 数据
     */
    @Schema(description = "数据")
    private T data;

    /**
     * 成功响应
     *
     * @param data 数据
     * @param <R>  数据类型
     * @return API响应
     */
    public static <R> ApiResponse<R> success(R data) {
        return new ApiResponse<>(200, "操作成功", data);
    }

    /**
     * 成功响应（无数据）
     *
     * @return API响应
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(200, "操作成功", null);
    }

    /**
     * 错误响应
     *
     * @param code    状态码
     * @param message 错误消息
     * @return API响应
     */
    public static <R> ApiResponse<R> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * 错误响应（500错误）
     *
     * @param message 错误消息
     * @return API响应
     */
    public static <R> ApiResponse<R> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
}
