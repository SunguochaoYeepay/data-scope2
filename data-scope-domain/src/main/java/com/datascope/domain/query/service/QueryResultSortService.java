package com.datascope.domain.query.service;

import com.datascope.domain.query.model.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * 查询结果排序服务接口
 */
public interface QueryResultSortService {

    /**
     * 对查询结果进行排序
     *
     * @param result     原始查询结果
     * @param sortFields 排序字段列表
     * @return 排序后的查询结果
     */
    QueryResult sort(QueryResult result, List<SortField> sortFields);

    /**
     * 对查询结果进行排序（单字段）
     *
     * @param result    原始查询结果
     * @param fieldName 排序字段名
     * @param direction 排序方向
     * @return 排序后的查询结果
     */
    default QueryResult sort(QueryResult result, String fieldName, SortDirection direction) {
        return sort(result, List.of(new SortField(fieldName, direction)));
    }

    /**
     * 对数据行列表进行排序
     *
     * @param rows       数据行列表
     * @param sortFields 排序字段列表
     * @return 排序后的数据行列表
     */
    List<Map<String, Object>> sortRows(List<Map<String, Object>> rows, List<SortField> sortFields);

    /**
     * 对数据行列表进行排序（单字段）
     *
     * @param rows      数据行列表
     * @param fieldName 排序字段名
     * @param direction 排序方向
     * @return 排序后的数据行列表
     */
    default List<Map<String, Object>> sortRows(List<Map<String, Object>> rows, String fieldName, SortDirection direction) {
        return sortRows(rows, List.of(new SortField(fieldName, direction)));
    }

    /**
     * 排序方向枚举
     */
    enum SortDirection {
        ASC,    // 升序
        DESC    // 降序
    }

    /**
     * 排序字段定义
     */
    class SortField {
        private final String fieldName;
        private final SortDirection direction;

        public SortField(String fieldName, SortDirection direction) {
            this.fieldName = fieldName;
            this.direction = direction;
        }

        public String getFieldName() {
            return fieldName;
        }

        public SortDirection getDirection() {
            return direction;
        }
    }
}
