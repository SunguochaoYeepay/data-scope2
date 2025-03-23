package com.datascope.domain.query.enums;

/**
 * Data masking type enum
 * 
 * @author dreambt
 */
public enum MaskType {
    /**
     * No masking
     */
    NONE,

    /**
     * Full masking (e.g., ****)
     */
    FULL,

    /**
     * Left masking (e.g., ***4567)
     */
    LEFT,

    /**
     * Right masking (e.g., 1234***)
     */
    RIGHT,

    /**
     * Middle masking (e.g., 12***67)
     */
    MIDDLE,

    /**
     * Custom masking pattern
     */
    CUSTOM
}
