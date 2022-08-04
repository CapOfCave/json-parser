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
        JsonElement result = JsonParser.parse(" [ \"someElement\" ] ");
        assertThat(result)
                .isInstanceOf(JsonElement.JsonArray.class)
                .extracting(jsonElement -> ((JsonElement.JsonArray) jsonElement).elements())
                .asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .hasSize(1);
        assertThat(((JsonElement.JsonArray) result).get(0)).isEqualTo(new JsonElement.JsonString("someElement"));
    }

}