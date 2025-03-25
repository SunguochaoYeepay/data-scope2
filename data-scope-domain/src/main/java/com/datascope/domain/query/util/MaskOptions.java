package com.datascope.domain.query.util;

import com.datascope.domain.query.enums.MaskType;

/**
 * Data masking configuration options
 */
public class MaskOptions {
    private final MaskType type;
    private final String maskChar;
    private final Integer keepPrefix;
    private final Integer keepSuffix;

    private MaskOptions(MaskType type, String maskChar, Integer keepPrefix, Integer keepSuffix) {
        this.type = type;
        this.maskChar = maskChar != null ? maskChar : "*";
        this.keepPrefix = keepPrefix != null ? keepPrefix : 0;
        this.keepSuffix = keepSuffix != null ? keepSuffix : 0;
    }

    public static MaskOptions phone() {
        return new MaskOptions(MaskType.PHONE, "*", 3, 4);
    }

    public static MaskOptions email() {
        return new MaskOptions(MaskType.EMAIL, "*", 1, 0);
    }

    public static MaskOptions idCard() {
        return new MaskOptions(MaskType.ID_CARD, "*", 4, 4);
    }

    public static MaskOptions bankCard() {
        return new MaskOptions(MaskType.BANK_CARD, "*", 4, 4);
    }

    public static MaskOptions custom(String maskChar, Integer keepPrefix, Integer keepSuffix) {
        return new MaskOptions(MaskType.CUSTOM, maskChar, keepPrefix, keepSuffix);
    }

    public MaskType getType() {
        return type;
    }

    public String getMaskChar() {
        return maskChar;
    }

    public Integer getKeepPrefix() {
        return keepPrefix;
    }

    public Integer getKeepSuffix() {
        return keepSuffix;
    }
}
