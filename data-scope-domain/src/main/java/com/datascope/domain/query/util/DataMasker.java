package com.datascope.domain.query.util;

import org.apache.commons.lang3.StringUtils;

import com.datascope.domain.query.enums.MaskType;

/**
 * Data masking utility
 * 
 * @author dreambt
 */
public class DataMasker {
    private static final char MASK_CHAR = '*';

    /**
     * Mask data according to mask type and configuration
     *
     * @param data Original data
     * @param maskType Mask type
     * @param maskConfig Mask configuration
     * @return Masked data
     */
    public static String mask(String data, MaskType maskType, String maskConfig) {
        if (StringUtils.isBlank(data) || maskType == null || maskType == MaskType.NONE) {
            return data;
        }

        switch (maskType) {
            case FULL:
                return StringUtils.repeat(MASK_CHAR, data.length());
            case LEFT:
                return maskLeft(data);
            case RIGHT:
                return maskRight(data);
            case MIDDLE:
                return maskMiddle(data);
            case CUSTOM:
                return maskCustom(data, maskConfig);
            default:
                return data;
        }
    }

    private static String maskLeft(String data) {
        int length = data.length();
        int maskLength = length / 2;
        return StringUtils.repeat(MASK_CHAR, maskLength) + data.substring(maskLength);
    }

    private static String maskRight(String data) {
        int length = data.length();
        int maskLength = length / 2;
        return data.substring(0, length - maskLength) + StringUtils.repeat(MASK_CHAR, maskLength);
    }

    private static String maskMiddle(String data) {
        int length = data.length();
        int preserveLength = length / 3;
        if (preserveLength == 0) {
            return StringUtils.repeat(MASK_CHAR, length);
        }
        String prefix = data.substring(0, preserveLength);
        String suffix = data.substring(length - preserveLength);
        return prefix + StringUtils.repeat(MASK_CHAR, length - 2 * preserveLength) + suffix;
    }

    private static String maskCustom(String data, String maskConfig) {
        if (StringUtils.isBlank(maskConfig)) {
            return data;
        }

        // Format: start,length
        String[] parts = maskConfig.split(",");
        if (parts.length != 2) {
            return data;
        }

        try {
            int start = Integer.parseInt(parts[0]);
            int maskLength = Integer.parseInt(parts[1]);
            int dataLength = data.length();

            if (start < 0 || maskLength <= 0 || start >= dataLength) {
                return data;
            }

            int end = Math.min(start + maskLength, dataLength);
            return data.substring(0, start) + 
                   StringUtils.repeat(MASK_CHAR, end - start) + 
                   data.substring(end);
        } catch (NumberFormatException e) {
            return data;
        }
    }
}
