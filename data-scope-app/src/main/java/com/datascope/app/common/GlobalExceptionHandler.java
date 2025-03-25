package com.datascope.app.common;

import com.datascope.app.response.ApiResponse;
import com.datascope.domain.query.exception.DataExecutionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     *
     * @param ex 参数校验异常
     * @return 错误响应
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ApiResponse<>(400, "参数校验失败", errors);
    }

    /**
     * 处理绑定异常
     *
     * @param ex 绑定异常
     * @return 错误响应
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ApiResponse<Map<String, String>> handleBindExceptions(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ApiResponse<>(400, "参数绑定失败", errors);
    }

    /**
     * 处理数据执行异常
     *
     * @param ex 数据执行异常
     * @return 错误响应
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(DataExecutionException.class)
    public ApiResponse<ErrorResponse> handleDataExecutionException(DataExecutionException ex) {
        log.error("数据执行异常", ex);
        return new ApiResponse<>(500, ex.getMessage(), new ErrorResponse(ex.getMessage()));
    }

    /**
     * 处理通用异常
     *
     * @param ex 异常
     * @return 错误响应
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<ErrorResponse> handleException(Exception ex) {
        log.error("系统异常", ex);
        return new ApiResponse<>(500, "系统异常，请联系管理员", new ErrorResponse(ex.getMessage()));
    }

    /**
     * 错误响应内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String detail;
    }
}
