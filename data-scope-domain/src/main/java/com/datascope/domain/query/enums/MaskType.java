package com.datascope.domain.query.enums;

/**
 * Data masking type enumeration
 */
public enum MaskType {
    NONE,           // No masking
    FULL,           // Full masking (all characters)
    LEFT,           // Mask left part
    RIGHT,          // Mask right part
    MIDDLE,         // Mask middle part
    EMAIL,          // Mask email address
    PHONE,          // Mask phone number
    ID_CARD,        // Mask ID card number
    BANK_CARD,      // Mask bank card number
    CUSTOM          // Custom masking pattern
}
