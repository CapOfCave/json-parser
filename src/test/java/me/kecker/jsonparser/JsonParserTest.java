package me.kecker.jsonparser;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParserTest {

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

    @Test
    @DisplayName("parse should return String value for quoted string")
    void testParseForQuotedStringInput() {
        Object result = JsonParser.parse("\"string\"");
        assertThat(result)
                .isInstanceOf(String.class)
                .isEqualTo("string");
    }

}