package com.datascope.domain.query.util;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Function;

/**
 * 掩码选项
 */
@Getter
@Builder
public class MaskOptions {

    /**
     * 保留左侧字符数
     */
    private int leftKeep;

    /**
     * 保留右侧字符数
     */
    private int rightKeep;

    /**
     * 自定义掩码处理器
     */
    private Function<String, String> customMasker;

    /**
     * 创建默认掩码选项
     */
    public static MaskOptions defaultOptions() {
        return MaskOptions.builder()
                .leftKeep(1)
                .rightKeep(1)
                .build();
    }

    /**
     * 创建邮箱掩码选项
     */
    public static MaskOptions emailOptions() {
        return MaskOptions.builder()
                .leftKeep(1)
                .rightKeep(1)
                .build();
    }

    /**
     * 创建手机号掩码选项
     */
    public static MaskOptions mobileOptions() {
        return MaskOptions.builder()
                .leftKeep(3)
                .rightKeep(4)
                .build();
    }

    /**
     * 创建身份证号掩码选项
     */
    public static MaskOptions idCardOptions() {
        return MaskOptions.builder()
                .leftKeep(6)
                .rightKeep(4)
                .build();
    }

    /**
     * 创建银行卡号掩码选项
     */
    public static MaskOptions bankCardOptions() {
        return MaskOptions.builder()
                .leftKeep(6)
                .rightKeep(4)
                .build();
    }
}