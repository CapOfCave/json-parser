package me.kecker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParseStateTest {


    @Test
    @DisplayName("Newly created object looks at first char")
    public void testAdvance() {
        JsonParseState parserState = new JsonParseState("ABCD");
        assertThat(parserState.current()).isEqualTo('A');

    }

}