package me.kecker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserTest {


    @Test
    @DisplayName("parseBoolean should return true for input 'true'")
    void testParseBooleanTrue() {
        boolean result = JsonParser.parseBoolean("true");
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("parseBoolean should return false for input 'false'")
    void testParseBooleanFalse() {
        boolean result = JsonParser.parseBoolean("false");
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("parseBoolean should throw an exception for an input different from true or false")
    void testParseBooleanThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> JsonParser.parseBoolean("other"));
    }

    @Test
    @DisplayName("parseString should return the input string without quotes")
    void testParseString() {
        String result = JsonParser.parseString("\"input\"");
        assertThat(result).isEqualTo("input");
    }

    @Test
    @DisplayName("parseObject should parse empty object")
    void testParseEmptyObject() {
        Map<String, Object> result = JsonParser.parseObject("{}");
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("parse should return Boolean value for input 'true'")
    void testParseForInputTrue() {
        Object result = JsonParser.parse("true");
        assertThat(result)
                .isInstanceOf(Boolean.class)
                .isEqualTo(Boolean.TRUE);
    }

    @Test
    @DisplayName("parse should return Boolean value for input 'false'")
    void testParseForInputFalse() {
        Object result = JsonParser.parse("false");
        assertThat(result)
                .isInstanceOf(Boolean.class)
                .isEqualTo(Boolean.FALSE);
    }
}