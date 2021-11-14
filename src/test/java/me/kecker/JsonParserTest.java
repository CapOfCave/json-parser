package me.kecker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParserTest {


    @Test
    @DisplayName("parseBoolean should return true for true values")
    void testParseBoolean() {
        boolean result = JsonParser.parseBoolean("true");
        assertThat(result).isEqualTo(true);
    }


}