package me.kecker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

}