package me.kecker.jsonparser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberUtilsTest {

    @Test
    @DisplayName("toDecimal() should return the decimal value")
    void testToDecimal() {
        int value = NumberUtils.toDecimal('0');
        assertThat(value).isEqualTo(0);
    }
}