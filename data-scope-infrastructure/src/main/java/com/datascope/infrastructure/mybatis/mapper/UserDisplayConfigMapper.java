package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDisplayConfigMapper {

    UserDisplayConfig selectById(@Param("id") String id);

    List<UserDisplayConfig> selectByUserId(@Param("userId") String userId);

    List<UserDisplayConfig> selectByDataSourceId(@Param("dataSourceId") String dataSourceId);

    List<UserDisplayConfig> selectByUserIdAndDataSourceId(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId);

    List<UserDisplayConfig> selectByUserIdAndDataSourceIdAndTableName(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);

    List<UserDisplayConfig> selectByUserIdAndDataSourceIdAndTableNameAndColumnName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName,
            @Param("columnName") String columnName);

    void insert(UserDisplayConfig entity);

    void update(UserDisplayConfig entity);

    void deleteById(@Param("id") String id);

    void deleteByUserId(@Param("userId") String userId);

    void deleteByDataSourceId(@Param("dataSourceId") String dataSourceId);

    void deleteByUserIdAndDataSourceId(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId);

    void deleteByUserIdAndDataSourceIdAndTableName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);

    void copyConfigs(
        @Param("fromUserId") String fromUserId,
        @Param("toUserId") String toUserId);
}
