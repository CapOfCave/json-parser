package me.kecker.jsonparser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public sealed interface JsonElement {

    record JsonNull() implements JsonElement {
        public static JsonNull INSTANCE = new JsonNull();
    }

    record JsonBoolean(boolean value) implements JsonElement, Comparable<JsonBoolean> {

        public static JsonBoolean TRUE = new JsonBoolean(true);

        public static JsonBoolean FALSE = new JsonBoolean(false);

        @Override
        public int compareTo(JsonBoolean o) {
            return Boolean.compare(this.value, o.value);
        }
    }

    record JsonNumber(BigDecimal value) implements JsonElement, Comparable<JsonNumber> {

        public JsonNumber(String val) {
            this(new BigDecimal(val));
        }

        @Override
        public int compareTo(JsonNumber o) {
            return this.value.compareTo(o.value);
        }
    }

    record JsonString(String value) implements JsonElement, Comparable<JsonString> {
        @Override
        public int compareTo(JsonString o) {
            return value.compareTo(o.value);
        }
    }

    record JsonObject(Map<String, JsonElement> members) implements JsonElement {
        public JsonElement get(String key) {
            return members.get(key);
        }
    }

    record JsonArray(List<JsonElement> elements) implements JsonElement {
        public JsonElement get(int index) {
            return elements.get(index);
        }
    }

    JsonNull NULL = JsonNull.INSTANCE;

}
