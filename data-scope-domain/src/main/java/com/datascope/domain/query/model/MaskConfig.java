package com.datascope.domain.query.model;

import com.datascope.domain.query.enums.MaskPosition;
import lombok.Getter;

/**
 * 掩码配置
 */
@Getter
public class MaskConfig {

    /**
     * 掩码位置
     */
    private final MaskPosition position;

    /**
     * 起始位置（从0开始）
     */
    private final Integer start;

    /**
     * 掩码长度
     */
    private final Integer length;

    /**
     * 掩码字符
     */
    private final String maskChar;

    private MaskConfig(MaskPosition position, Integer start, Integer length, String maskChar) {
        this.position = position;
        this.start = start;
        this.length = length;
        this.maskChar = maskChar;
    }

    /**
     * 创建掩码配置
     *
     * @param position 掩码位置
     * @param start 起始位置
     * @param length 掩码长度
     * @param maskChar 掩码字符
     * @return 掩码配置
     */
    public static MaskConfig of(MaskPosition position, Integer start, Integer length, String maskChar) {
        return new MaskConfig(position, start, length, maskChar);
    }

    /**
     * 创建默认配置
     *
     * @return 默认配置
     */
    public static MaskConfig createDefault() {
        return of(MaskPosition.MIDDLE, 0, 4, "*");
    }

    /**
     * 创建手机号掩码配置
     *
     * @return 手机号掩码配置
     */
    public static MaskConfig createMobile() {
        return of(MaskPosition.MIDDLE, 3, 4, "*");
    }

    /**
     * 创建邮箱掩码配置
     *
     * @return 邮箱掩码配置
     */
    public static MaskConfig createEmail() {
        return of(MaskPosition.MIDDLE, 3, 4, "*");
    }

    /**
     * 创建身份证号掩码配置
     *
     * @return 身份证号掩码配置
     */
    public static MaskConfig createIdCard() {
        return of(MaskPosition.MIDDLE, 6, 8, "*");
    }

    /**
     * 创建银行卡号掩码配置
     *
     * @return 银行卡号掩码配置
     */
    public static MaskConfig createBankCard() {
        return of(MaskPosition.MIDDLE, 4, 8, "*");
    }

    /**
     * 创建姓名掩码配置
     *
     * @return 姓名掩码配置
     */
    public static MaskConfig createName() {
        return of(MaskPosition.END, 0, 1, "*");
    }

    /**
     * 创建地址掩码配置
     *
     * @return 地址掩码配置
     */
    public static MaskConfig createAddress() {
        return of(MaskPosition.START, 6, 8, "*");
    }
}