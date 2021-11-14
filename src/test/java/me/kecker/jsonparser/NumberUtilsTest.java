package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberUtilsTest {

    @Test
    @DisplayName("toDecimal() should return the decimal value")
    void testToDecimal() throws IllegalNumberException {
        int value = NumberUtils.toDecimal('0');
        assertThat(value).isEqualTo(0);
    }

    @ParameterizedTest
    @DisplayName("toDecimal() should throw exception for non-numeric characters")
    @ValueSource(chars = {'%', 'A'})
    void testToDecimalInvalidValues(char input) {
        assertThrows(IllegalNumberException.class, () -> NumberUtils.toDecimal(input));
    }
}