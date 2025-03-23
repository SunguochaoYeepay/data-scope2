package com.datascope.domain.datasource.model;

import java.util.UUID;

/**
 * 数据源ID值对象
 */
public class DataSourceId {
    private final UUID value;

    public DataSourceId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("DataSource ID cannot be null");
        }
        this.value = value;
    }

    public static DataSourceId create() {
        return new DataSourceId(UUID.randomUUID());
    }

    public static DataSourceId of(UUID value) {
        return new DataSourceId(value);
    }

    public static DataSourceId of(String value) {
        return new DataSourceId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSourceId that = (DataSourceId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}