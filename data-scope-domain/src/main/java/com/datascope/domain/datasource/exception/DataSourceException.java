package com.datascope.domain.datasource.exception;

import com.datascope.domain.common.exception.BusinessException;

/**
 * 数据源异常类
 */
public class DataSourceException extends BusinessException {

    private static final String ERROR_CODE_PREFIX = "DATASOURCE_";

    private DataSourceException(String code, String message) {
        super(ERROR_CODE_PREFIX + code, message);
    }

    private DataSourceException(String code, String message, Object data) {
        super(ERROR_CODE_PREFIX + code, message, data);
    }

    private DataSourceException(String code, String message, Throwable cause) {
        super(ERROR_CODE_PREFIX + code, message, cause);
    }

    /**
     * 数据源不存在异常
     *
     * @param id 数据源ID
     * @return 异常实例
     */
    public static DataSourceException notFound(String id) {
        return new DataSourceException("NOT_FOUND", String.format("数据源[%s]不存在", id));
    }

    /**
     * 数据源名称已存在异常
     *
     * @param name 数据源名称
     * @return 异常实例
     */
    public static DataSourceException nameExists(String name) {
        return new DataSourceException("NAME_EXISTS", String.format("数据源名称[%s]已存在", name));
    }

    /**
     * 数据源连接失败异常
     *
     * @param id    数据源ID
     * @param cause 原始异常
     * @return 异常实例
     */
    public static DataSourceException connectionFailed(String id, Throwable cause) {
        return new DataSourceException("CONNECTION_FAILED", 
            String.format("数据源[%s]连接失败", id), cause);
    }

    /**
     * 数据源元数据同步失败异常
     *
     * @param id    数据源ID
     * @param cause 原始异常
     * @return 异常实例
     */
    public static DataSourceException metadataSyncFailed(String id, Throwable cause) {
        return new DataSourceException("METADATA_SYNC_FAILED", 
            String.format("数据源[%s]元数据同步失败", id), cause);
    }

    /**
     * 数据源查询超时异常
     *
     * @param id 数据源ID
     * @return 异常实例
     */
    public static DataSourceException queryTimeout(String id) {
        return new DataSourceException("QUERY_TIMEOUT", 
            String.format("数据源[%s]查询超时", id));
    }

    /**
     * 数据源查询结果超出限制异常
     *
     * @param id    数据源ID
     * @param limit 限制数量
     * @return 异常实例
     */
    public static DataSourceException resultLimitExceeded(String id, int limit) {
        return new DataSourceException("RESULT_LIMIT_EXCEEDED", 
            String.format("数据源[%s]查询结果超出限制[%d]", id, limit));
    }

    /**
     * 数据源查询频率超出限制异常
     *
     * @param id 数据源ID
     * @return 异常实例
     */
    public static DataSourceException rateLimitExceeded(String id) {
        return new DataSourceException("RATE_LIMIT_EXCEEDED", 
            String.format("数据源[%s]查询频率超出限制", id));
    }

    /**
     * 数据源类型不支持异常
     *
     * @param type 数据源类型
     * @return 异常实例
     */
    public static DataSourceException typeNotSupported(String type) {
        return new DataSourceException("TYPE_NOT_SUPPORTED", 
            String.format("不支持的数据源类型[%s]", type));
    }
}