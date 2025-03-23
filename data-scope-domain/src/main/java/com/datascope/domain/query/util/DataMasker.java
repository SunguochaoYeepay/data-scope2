package com.datascope.domain.query.util;

import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datascope.domain.query.enums.MaskType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 数据掩码工具类
 */
public class DataMasker {

    private static final Logger log = LoggerFactory.getLogger(DataMasker.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 掩码处理
     *
     * @param value    原始值
     * @param type     掩码类型
     * @param config   掩码配置
     * @return 掩码后的值
     */
    public static String mask(String value, MaskType type, String config) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        try {
            switch (type) {
                case NONE:
                    return value;
                case MOBILE:
                    return maskMobile(value);
                case ID_CARD:
                    return maskIdCard(value);
                case BANK_CARD:
                    return maskBankCard(value);
                case EMAIL:
                    return maskEmail(value);
                case NAME:
                    return maskName(value);
                case CUSTOM:
                    return maskCustom(value, config);
                default:
                    return value;
            }
        } catch (Exception e) {
            log.error("掩码处理失败: " + value, e);
            return value;
        }
    }

    private static String maskMobile(String mobile) {
        if (!Pattern.matches("^\\d{11}$", mobile)) {
            return mobile;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    private static String maskIdCard(String idCard) {
        if (!Pattern.matches("^\\d{17}[0-9X]$", idCard)) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    private static String maskBankCard(String bankCard) {
        if (!Pattern.matches("^\\d{16,19}$", bankCard)) {
            return bankCard;
        }
        return "**** **** **** " + bankCard.substring(bankCard.length() - 4);
    }

    private static String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        return name.charAt(0) + "****" + domain;
    }

    private static String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name;
        }
        return name.charAt(0) + "*".repeat(name.length() - 1);
    }

    private static String maskCustom(String value, String config) {
        try {
            Map<String, Object> maskConfig = MAPPER.readValue(config, Map.class);
            String pattern = (String) maskConfig.get("pattern");
            String replacement = (String) maskConfig.get("replacement");
            return value.replaceAll(pattern, replacement);
        } catch (Exception e) {
            log.error("自定义掩码处理失败: " + value, e);
            return value;
        }
    }
}