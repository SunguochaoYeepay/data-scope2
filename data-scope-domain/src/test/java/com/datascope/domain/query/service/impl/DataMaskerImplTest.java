package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.enums.MaskType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DataMaskerImplTest {

    private DataMaskerImpl dataMasker;

    @BeforeEach
    void setUp() {
        dataMasker = new DataMaskerImpl();
    }

    @Test
    void shouldReturnOriginalValueWhenInputIsNull() {
        assertThat(dataMasker.mask((String) null, MaskType.FULL)).isNull();
    }

    @Test
    void shouldReturnOriginalValueWhenInputIsEmpty() {
        assertThat(dataMasker.mask("", MaskType.FULL)).isEmpty();
    }

    @Test
    void shouldReturnOriginalValueWhenMaskTypeIsNull() {
        assertThat(dataMasker.mask("test", (MaskType) null)).isEqualTo("test");
    }

    @Test
    void shouldReturnOriginalValueWhenMaskTypeIsNone() {
        assertThat(dataMasker.mask("test", MaskType.NONE)).isEqualTo("test");
    }

    @Test
    void shouldMaskFullValue() {
        assertThat(dataMasker.mask("test", MaskType.FULL)).isEqualTo("****");
    }

    @Test
    void shouldMaskLeftPart() {
        assertThat(dataMasker.mask("test", MaskType.LEFT)).isEqualTo("*est");
    }

    @Test
    void shouldMaskRightPart() {
        assertThat(dataMasker.mask("test", MaskType.RIGHT)).isEqualTo("tes*");
    }

    @Test
    void shouldMaskMiddlePart() {
        assertThat(dataMasker.mask("test", MaskType.MIDDLE)).isEqualTo("t**t");
    }

    @Test
    void shouldMaskEmail() {
        assertThat(dataMasker.mask("test@example.com", MaskType.EMAIL))
            .isEqualTo("t***@example.com");
    }

    @Test
    void shouldNotMaskInvalidEmail() {
        assertThat(dataMasker.mask("@test.com", MaskType.EMAIL))
            .isEqualTo("@test.com");
    }

    @Test
    void shouldMaskPhoneNumber() {
        assertThat(dataMasker.mask("13812345678", MaskType.PHONE))
            .isEqualTo("138****5678");
    }

    @Test
    void shouldNotMaskShortPhoneNumber() {
        assertThat(dataMasker.mask("12345", MaskType.PHONE))
            .isEqualTo("12345");
    }

    @Test
    void shouldMaskIdCard() {
        assertThat(dataMasker.mask("310101199001011234", MaskType.ID_CARD))
            .isEqualTo("310101********1234");
    }

    @Test
    void shouldNotMaskShortIdCard() {
        assertThat(dataMasker.mask("12345", MaskType.ID_CARD))
            .isEqualTo("12345");
    }

    @Test
    void shouldMaskBankCard() {
        assertThat(dataMasker.mask("6222021234567890123", MaskType.BANK_CARD))
            .isEqualTo("622202*********0123");
    }

    @Test
    void shouldNotMaskShortBankCard() {
        assertThat(dataMasker.mask("12345", MaskType.BANK_CARD))
            .isEqualTo("12345");
    }

    @Test
    void shouldNotMaskCustomType() {
        assertThat(dataMasker.mask("test", MaskType.CUSTOM))
            .isEqualTo("test");
    }
}
