package com.datascope.domain.query.exception;

/**
 * 数据执行异常，用于处理SQL执行过程中的错误
 */
public class DataExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataExecutionException(String message) {
        super(message);
    }

    public DataExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}