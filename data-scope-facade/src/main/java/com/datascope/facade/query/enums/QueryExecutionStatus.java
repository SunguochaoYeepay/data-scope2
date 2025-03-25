package com.datascope.facade.query.enums;

/**
 * Query execution status enumeration
 */
public enum QueryExecutionStatus {
    NOT_STARTED,
    RUNNING,
    COMPLETED,
    FAILED,
    CANCELLED,
    TIMEOUT,
    NOT_EXECUTED
}