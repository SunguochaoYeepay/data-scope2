package com.datascope.domain.query.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 数据掩码配置
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaskOptions {
    /**
     * 掩码类型
     */
    private MaskType type;

    /**
     * 掩码字符
     */
    private String maskChar;

    /**
     * 保留前几位
     */
    private Integer keepPrefix;

    /**
     * 保留后几位
     */
    private Integer keepSuffix;

    /**
     * 创建手机号掩码配置
     */
    public static MaskOptions phone() {
        return MaskOptions.builder()
            .type(MaskType.PHONE)
            .maskChar("*")
            .keepPrefix(3)
            .keepSuffix(4)
                .build();
    }

    /**
     * 创建邮箱掩码配置
     */
    public static MaskOptions email() {
        return MaskOptions.builder()
            .type(MaskType.EMAIL)
            .maskChar("*")
            .keepPrefix(3)
            .keepSuffix(0)
                .build();
    }

    /**
     * 创建身份证掩码配置
     */
    public static MaskOptions idCard() {
        return MaskOptions.builder()
            .type(MaskType.ID_CARD)
            .maskChar("*")
            .keepPrefix(6)
            .keepSuffix(4)
                .build();
    }

    /**
     * 创建银行卡掩码配置
     */
    public static MaskOptions bankCard() {
        return MaskOptions.builder()
            .type(MaskType.BANK_CARD)
            .maskChar("*")
            .keepPrefix(4)
            .keepSuffix(4)
                .build();
    }

    /**
     * 创建自定义掩码配置
     */
    public static MaskOptions custom(String maskChar, Integer keepPrefix, Integer keepSuffix) {
        return MaskOptions.builder()
            .type(MaskType.CUSTOM)
            .maskChar(maskChar)
            .keepPrefix(keepPrefix)
            .keepSuffix(keepSuffix)
                .build();
    }
}
