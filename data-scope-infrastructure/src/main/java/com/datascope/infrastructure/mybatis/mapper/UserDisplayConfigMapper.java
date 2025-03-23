package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.infrastructure.entity.UserDisplayConfigEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDisplayConfigMapper {

    UserDisplayConfigEntity selectById(@Param("id") String id);

    List<UserDisplayConfigEntity> selectByUserId(@Param("userId") String userId);

    List<UserDisplayConfigEntity> selectByDataSourceId(@Param("dataSourceId") String dataSourceId);

    List<UserDisplayConfigEntity> selectByUserIdAndDataSourceId(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId);

    List<UserDisplayConfigEntity> selectByUserIdAndDataSourceIdAndTableName(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);

    List<UserDisplayConfigEntity> selectByUserIdAndDataSourceIdAndTableNameAndColumnName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName,
            @Param("columnName") String columnName);

    void insert(UserDisplayConfigEntity entity);

    void update(UserDisplayConfigEntity entity);

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
