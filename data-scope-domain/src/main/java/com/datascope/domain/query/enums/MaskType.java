package com.datascope.domain.query.enums;

/**
 * 数据掩码类型
 */
public enum MaskType {
    /**
     * 无掩码
     */
    NONE,

    /**
     * 手机号掩码
     * 示例: 138****1234
     */
    MOBILE,

    /**
     * 身份证号掩码
     * 示例: 110101********1234
     */
    ID_CARD,

    /**
     * 银行卡号掩码
     * 示例: **** **** **** 1234
     */
    BANK_CARD,

    /**
     * 邮箱掩码
     * 示例: a****@example.com
     */
    EMAIL,

    /**
     * 姓名掩码
     * 示例: 张*
     */
    NAME,

    /**
     * 自定义掩码
     * 需要配置掩码规则
     */
    CUSTOM
}