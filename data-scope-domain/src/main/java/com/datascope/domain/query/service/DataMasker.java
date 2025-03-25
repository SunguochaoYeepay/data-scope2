package com.datascope.domain.query.service;

import com.datascope.domain.query.enums.MaskType;
import com.datascope.domain.query.util.MaskOptions;

/**
 * Data masking service interface
 */
public interface DataMasker {

    /**
     * Mask data according to the specified mask type
     *
     * @param value    The value to be masked
     * @param maskType The type of masking to apply
     * @return The masked value
     */
    String mask(String value, MaskType maskType);

    /**
     * Mask data according to the specified mask options
     *
     * @param value   The value to be masked
     * @param options The masking options
     * @return The masked value
     */
    default String mask(String value, MaskOptions options) {
        if (value == null || value.isEmpty() || options == null) {
            return value;
        }
        return mask(value, options.getType());
    }
}
