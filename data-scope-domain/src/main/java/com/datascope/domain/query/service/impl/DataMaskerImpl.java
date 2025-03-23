package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.enums.MaskType;
import com.datascope.domain.query.service.DataMasker;
import com.datascope.domain.query.util.MaskOptions;
import org.springframework.stereotype.Service;

/**
 * Implementation of data masking service
 */
@Service
public class DataMaskerImpl implements DataMasker {

    @Override
    public String mask(String value, MaskType maskType) {
        if (value == null || value.isEmpty() || maskType == null) {
            return value;
        }

        switch (maskType) {
            case NONE:
                return value;
            case FULL:
                return maskFull(value);
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
                // Custom masking should be configured through MaskOptions
                return value;
            default:
                return value;
        }
    }

    @Override
    public String mask(String value, MaskOptions options) {
        if (value == null || value.isEmpty() || options == null) {
            return value;
        }

        if (options.getType() == MaskType.CUSTOM) {
            return maskWithPattern(value, options.getMaskChar(), options.getKeepPrefix(), options.getKeepSuffix());
        }

        return mask(value, options.getType());
    }

    private String maskFull(String value) {
        return "*".repeat(value.length());
    }

    private String maskLeft(String value) {
        int maskLength = value.length() / 3;
        return "*".repeat(maskLength) + value.substring(maskLength);
    }

    private String maskRight(String value) {
        int maskLength = value.length() / 3;
        return value.substring(0, value.length() - maskLength) + "*".repeat(maskLength);
    }

    private String maskMiddle(String value) {
        int length = value.length();
        int maskLength = length / 3;
        int start = (length - maskLength) / 2;
        return value.substring(0, start) + "*".repeat(maskLength) + value.substring(start + maskLength);
    }

    private String maskEmail(String value) {
        int atIndex = value.indexOf('@');
        if (atIndex <= 1) {
            return value;
        }
        String name = value.substring(0, atIndex);
        String domain = value.substring(atIndex);
        return name.charAt(0) + "*".repeat(name.length() - 1) + domain;
    }

    private String maskPhone(String value) {
        if (value.length() < 7) {
            return value;
        }
        return value.substring(0, 3) + "*".repeat(value.length() - 7) + value.substring(value.length() - 4);
    }

    private String maskIdCard(String value) {
        if (value.length() < 8) {
            return value;
        }
        return value.substring(0, 4) + "*".repeat(value.length() - 8) + value.substring(value.length() - 4);
    }

    private String maskBankCard(String value) {
        if (value.length() < 8) {
            return value;
        }
        return value.substring(0, 4) + "*".repeat(value.length() - 8) + value.substring(value.length() - 4);
    }

    private String maskWithPattern(String value, String maskChar, Integer keepPrefix, Integer keepSuffix) {
        int length = value.length();
        keepPrefix = keepPrefix != null ? keepPrefix : 0;
        keepSuffix = keepSuffix != null ? keepSuffix : 0;

        if (keepPrefix + keepSuffix >= length) {
            return value;
        }

        String mask = (maskChar != null ? maskChar : "*").repeat(length - keepPrefix - keepSuffix);
        return value.substring(0, keepPrefix) + mask + value.substring(length - keepSuffix);
    }
}