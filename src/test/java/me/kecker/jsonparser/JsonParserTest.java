package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.JsonParseException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonParserTest {

    @Test
    @DisplayName("parse should return json value")
    void testParseForInputTrue() throws JsonParseException {
        Object result = JsonParser.parse(" [ \"someElement\" ] ");
        assertThat(result)
                .asInstanceOf(InstanceOfAssertFactories.ARRAY)
                .hasSize(1);
        Object[] array = (Object[]) result;
        assertThat(array[0]).isEqualTo("someElement");
    }

}