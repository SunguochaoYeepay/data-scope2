package com.datascope.app.converter;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 枚举类型转换器
 * 用于Domain模块和Facade模块之间的枚举类型转换
 */
@Mapper
public interface EnumConverter {

    EnumConverter INSTANCE = Mappers.getMapper(EnumConverter.class);

    // ColumnAlign枚举转换
    default com.datascope.facade.query.enums.ColumnAlign toFacade(com.datascope.domain.query.enums.ColumnAlign domainEnum) {
        if (domainEnum == null) {
            return null;
        }
        return com.datascope.facade.query.enums.ColumnAlign.valueOf(domainEnum.name());
    }

    default com.datascope.domain.query.enums.ColumnAlign toDomain(com.datascope.facade.query.enums.ColumnAlign facadeEnum) {
        if (facadeEnum == null) {
            return null;
        }
        return com.datascope.domain.query.enums.ColumnAlign.valueOf(facadeEnum.name());
    }

    // ColumnFixed枚举转换
    default com.datascope.facade.query.enums.ColumnFixed toFacade(com.datascope.domain.query.enums.ColumnFixed domainEnum) {
        if (domainEnum == null) {
            return null;
        }
        return com.datascope.facade.query.enums.ColumnFixed.valueOf(domainEnum.name());
    }

    default com.datascope.domain.query.enums.ColumnFixed toDomain(com.datascope.facade.query.enums.ColumnFixed facadeEnum) {
        if (facadeEnum == null) {
            return null;
        }
        return com.datascope.domain.query.enums.ColumnFixed.valueOf(facadeEnum.name());
    }

    // MaskType枚举转换
    default com.datascope.facade.query.enums.MaskType toFacade(com.datascope.domain.query.enums.MaskType domainEnum) {
        if (domainEnum == null) {
            return null;
        }
        return com.datascope.facade.query.enums.MaskType.valueOf(domainEnum.name());
    }

    default com.datascope.domain.query.enums.MaskType toDomain(com.datascope.facade.query.enums.MaskType facadeEnum) {
        if (facadeEnum == null) {
            return null;
        }
        return com.datascope.domain.query.enums.MaskType.valueOf(facadeEnum.name());
    }

    // QueryExecutionStatus枚举转换
    default com.datascope.facade.query.enums.QueryExecutionStatus toFacade(com.datascope.domain.query.enums.QueryExecutionStatus domainEnum) {
        if (domainEnum == null) {
            return null;
        }
        return com.datascope.facade.query.enums.QueryExecutionStatus.valueOf(domainEnum.name());
    }

    default com.datascope.domain.query.enums.QueryExecutionStatus toDomain(com.datascope.facade.query.enums.QueryExecutionStatus facadeEnum) {
        if (facadeEnum == null) {
            return null;
        }
        return com.datascope.domain.query.enums.QueryExecutionStatus.valueOf(facadeEnum.name());
    }
}
