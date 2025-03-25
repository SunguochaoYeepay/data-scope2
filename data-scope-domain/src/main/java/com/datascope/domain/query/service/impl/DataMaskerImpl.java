package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.enums.MaskType;
import com.datascope.domain.query.service.DataMasker;
import com.datascope.domain.query.util.MaskOptions;
import org.springframework.stereotype.Service;

/**
 * Data masking service implementation
 */
@Service
public class DataMaskerImpl implements DataMasker {

    @Override
    public String mask(String value, MaskType type) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return "";
        }

        if (type == null) {
            return value;
        }
        switch (type) {
            case FULL:
                return maskAll(value);
            case LEFT:
                return maskLeft(value);
            case RIGHT:
                return maskRight(value);
            case MIDDLE:
                return maskMiddle(value);
            case EMAIL:
                return maskEmail(value);
            case PHONE:
                return maskPhone(value);
            case ID_CARD:
                return maskIdCard(value);
            case BANK_CARD:
                return maskBankCard(value);
            case CUSTOM:
                return value;
            default:
                return value;
        }
    }

    @Override
    public String mask(String value, MaskOptions options) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return "";
        }

        if (options.getType() == MaskType.CUSTOM) {
            return maskCustom(value, options);
        }

        return mask(value, options.getType());
    }

    private String maskAll(String value) {
        return "*".repeat(value.length());
    }

    private String maskLeft(String value) {
        return "*" + value.substring(1);
    }

    private String maskRight(String value) {
        return value.substring(0, value.length() - 1) + "*";
    }

    private String maskMiddle(String value) {
        if (value.length() <= 2) {
            return value;
        }
        return value.charAt(0) + "*".repeat(value.length() - 2) + value.charAt(value.length() - 1);
    }

    private String maskEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 0) {
            return value;
        }
        String name = value.substring(0, atIndex);
        String domain = value.substring(atIndex);
        return name.charAt(0) + "*".repeat(name.length() - 1) + domain;
    }

    private String maskPhone(String value) {
        if (value.length() != 11) {
            return value;
        }
        return value.substring(0, 3) + "*".repeat(4) + value.substring(7);
    }

    private String maskIdCard(String value) {
        if (value.length() != 18) {
            return value;
        }
        return value.substring(0, 6) + "*".repeat(8) + value.substring(value.length() - 4);
    }

    private String maskBankCard(String value) {
        if (value.length() < 16) {
            return value;
        }
        return value.substring(0, 6) + "*".repeat(9) + value.substring(value.length() - 4);
    }

    private String maskCustom(String value, MaskOptions options) {
        int prefixLen = options.getKeepPrefix();
        int suffixLen = options.getKeepSuffix();
        int maskLen = value.length() - prefixLen - suffixLen;

        if (maskLen <= 0) {
            return value;
        }

        return value.substring(0, prefixLen) +
            options.getMaskChar().repeat(maskLen) +
            value.substring(value.length() - suffixLen);
    }
}
