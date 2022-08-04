package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalNumberException;
import me.kecker.jsonparser.exceptions.IllegalTokenException;
import me.kecker.jsonparser.exceptions.JsonParseException;
import me.kecker.jsonparser.exceptions.UnexpectedCharacterException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParseStateTest {


    @Test
    @DisplayName("Newly created object looks at first char")
    public void testCurrent() {
        JsonParseState parserState = new JsonParseState("ABCD");
        assertThat(parserState.current()).isEqualTo('A');

    }

    @Test
    @DisplayName("Advance should make the next character current")
    public void testAdvance() {
        JsonParseState parserState = new JsonParseState("ABCD");
        parserState.advance();
        assertThat(parserState.current()).isEqualTo('B');

    }

    @Test
    @DisplayName("reachedEnd should be false if the parser has not reached the last char")
    public void testNotReachedEnd() {
        JsonParseState parserState = new JsonParseState("AB");
        boolean reachedEnd = parserState.reachedEnd();
        assertThat(reachedEnd).isEqualTo(false);
    }

    @Test
    @DisplayName("reachedEnd should be true if the parser has passed the last char")
    public void testReachedEnd() {
        JsonParseState parserState = new JsonParseState("A");
        parserState.advance();
        boolean reachedEnd = parserState.reachedEnd();
        assertThat(reachedEnd).isEqualTo(true);
    }

    @Test
    @DisplayName("whitespace should skip spaces")
    void testWhitespaceSkipsSpaces() {
        JsonParseState parserState = new JsonParseState("   a");
        parserState.whitespace();
        assertThat(parserState.current()).isEqualTo('a');
    }

    @ParameterizedTest
    @DisplayName("whitespace should skip different whitespace characters")
    @ValueSource(strings = {"\u0020", "\n", "\r", "\u0009"})
    public void testSkipWhitespaceCharacter(String whitespace) {
        JsonParseState parserState = new JsonParseState(whitespace + "a");
        parserState.whitespace();
        assertThat(parserState.current()).isEqualTo('a');
    }

    @Test
    @DisplayName("whitespace should skip trailing spaces")
    void testWhitespaceSkipsTrailingSpaces() {
        JsonParseState parserState = new JsonParseState("   ");
        parserState.whitespace();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("nullType() should pass for input 'null'")
    void testNullType() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("null");
        JsonElement.JsonNull result = parserState.nullType();
        assertThat(result).isEqualTo(JsonElement.NULL);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("nullType() should throw exception for any other input")
    void testNullTypeWrongInput() {
        JsonParseState parserState = new JsonParseState("other");
        assertThrows(IllegalTokenException.class, parserState::nullType);
    }

    @Test
    @DisplayName("bool() should return true for input 'true'")
    void testBooleanTrue() throws IllegalTokenException {
        JsonParseState parserState = new JsonParseState("true");
        JsonElement.JsonBoolean result = parserState.bool();
        assertThat(result).isEqualTo(JsonElement.JsonBoolean.TRUE);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("bool() should return false for input 'false'")
    void testBooleanFalse() throws IllegalTokenException {
        JsonParseState parserState = new JsonParseState("false");
        JsonElement.JsonBoolean result = parserState.bool();
        assertThat(result).isEqualTo(JsonElement.JsonBoolean.FALSE);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("number() should throw exception for non numeric input")
    @ValueSource(strings = {"A", "."})
    void testNumberNonNumericInput(String input) {
        JsonParseState parserState = new JsonParseState(input);
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should return Integer for integer input")
    void testNumberInteger() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("1234");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("1234");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should return 0 for input 0")
    void testNumber0() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("0");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("0");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should throw exception when first of many digits is 0")
    void testNumberIntegerStartingWith0() {
        JsonParseState parserState = new JsonParseState("012");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should return Integer for negative integer input")
    void testNumberNegativeInteger() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("-1234");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result).isEqualByComparingTo(new JsonElement.JsonNumber("-1234"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should return 0 for input -0")
    void testNumberNegative0() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("-0");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("0");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should throw exception when first of many digits is 0, even if it is negative")
    void testNumberNegativeIntegerStartingWith0() {
        JsonParseState parserState = new JsonParseState("-012");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should return Double for floating point input")
    void testNumberFloat() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("12.34");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("12.34");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should throw exception for trailing point.")
    void testNumberTrailingPoint() {
        JsonParseState parserState = new JsonParseState("12.");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should throw exception for trailing point before exponent.")
    void testNumberTrailingPointBeforeExponent() {
        JsonParseState parserState = new JsonParseState("12.e5");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should throw exception for trailing point after exponent.")
    void testNumberTrailingPointAfterExponent() {
        JsonParseState parserState = new JsonParseState("12e5.");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should throw exception for leading point.")
    void testNumberLeadingPoint() {
        JsonParseState parserState = new JsonParseState(".5");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should throw exception for leading point.")
    void testNegativeNumberLeadingPoint() {
        JsonParseState parserState = new JsonParseState("-.5");
        assertThrows(IllegalNumberException.class, parserState::number);
    }

    @Test
    @DisplayName("number() should allow exponents with lowercase e")
    void testNumberExponentWithLowercaseE() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("1e2");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("100");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should allow exponents with capital E")
    void testNumberExponentWithCapitalE() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("1E2");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("100");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() should not throw exception when having the form 0eXXX")
    void testNumberZeroWithExponent() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("0e5");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("0");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("number() input betweeen 0 and 1 should not throw exception ")
    void testNumberBetween0And1() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("0.1");
        JsonElement.JsonNumber result = parserState.number();
        assertThat(result.value()).isEqualByComparingTo("0.1");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("bool() should throw exception for any non-boolean input")
    void testBooleanOtherInput() {
        JsonParseState parserState = new JsonParseState("other");
        assertThrows(IllegalTokenException.class, parserState::bool);
    }

    @Test
    @DisplayName("string() should return the input string without quotes")
    void testParseString() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"input\"");
        JsonElement.JsonString result = parserState.string();
        assertThat(result.value()).isEqualTo("input");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("string() should throw exception if not starting with quotes")
    void testParseStringWithoutLeadingQuote() {
        JsonParseState parserState = new JsonParseState("input\"");
        assertThrows(UnexpectedCharacterException.class, parserState::string);
    }

    @Test
    @DisplayName("string() should return the input string while considering escaped quotes")
    void testParseStringWithEscapedQuote() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"a\\\"b\"");
        JsonElement.JsonString result = parserState.string();
        assertThat(result.value()).isEqualTo("a\"b");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("string() should return the input string while considering escaped backslashes")
    void testParseStringWithEscapedBackslash() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"a\\\\b\"");
        JsonElement.JsonString result = parserState.string();
        assertThat(result.value()).isEqualTo("a\\b");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @ParameterizedTest
    @DisplayName("string() should return the input string while considering escaped control characters")
    @MethodSource("provideInputForTestParseStringWithEscapedControlCharacters")
    void testParseStringWithEscapedControlCharacters(String input, String expected) throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"" + input + "\"");
        JsonElement.JsonString result = parserState.string();
        assertThat(result.value()).isEqualTo(expected);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("string() should return the input string while considering escaped unicode characters")
    void testParseStringWithEscapedUnicodeCharacters() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"a\\u0041\\u00Ae\"");
        JsonElement.JsonString result = parserState.string();
        assertThat(result.value()).isEqualTo("aA\u00AE");
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("string() should throw exception for illegal escape")
    void testParseStringIllegalEscape() {
        JsonParseState parserState = new JsonParseState("\"\\a\"");
        assertThrows(JsonParseException.class, parserState::string);
    }

    @Test
    @DisplayName("string() should throw exception for trailing backslash")
    void testParseStringTrailingBackslash() {
        JsonParseState parserState = new JsonParseState("\"\\");
        assertThrows(JsonParseException.class, parserState::string);
    }

    @Test
    @DisplayName("member() should return a key-value pair")
    void testMemberShouldReturnKeyValuePair() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"key\":\"value\"");
        Map.Entry<String, JsonElement> member = parserState.member();
        assertThat(member.getKey()).isEqualTo("key");
        assertThat(member.getValue()).isEqualTo(new JsonElement.JsonString("value"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);

    }

    @Test
    @DisplayName("member() should return a key-value pair ignoring whitespace")
    void testMemberShouldReturnKeyValuePairIgnoringWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState(" \"key\" : \"value\" ");
        Map.Entry<String, JsonElement> member = parserState.member();
        assertThat(member.getKey()).isEqualTo("key");
        assertThat(member.getValue()).isEqualTo(new JsonElement.JsonString("value"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("member() should allow null values")
    void testMemberShouldAllowNullValues() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"key\":null");
        Map.Entry<String, JsonElement> member = parserState.member();
        assertThat(member.getKey()).isEqualTo("key");
        assertThat(member.getValue()).isEqualTo(JsonElement.NULL);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should return empty Map for empty object")
    void testParseEmptyObject() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{}");
        JsonElement.JsonObject result = parserState.object();
        assertThat(result.members()).isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should return empty Map for empty object with whitespace")
    void testParseEmptyObjectWithWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{ \n}");
        JsonElement.JsonObject result = parserState.object();
        assertThat(result.members()).isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should throw exception if not starting with opening curly braces")
    void testParseEmptyObjectWithoutStartingBraces() {
        JsonParseState parserState = new JsonParseState(" }");
        assertThrows(UnexpectedCharacterException.class, parserState::object);
    }

    @Test
    @DisplayName("object() should throw exception if not ending with closing curly braces")
    void testParseEmptyObjectWithoutEndingBraces() {
        JsonParseState parserState = new JsonParseState("{ ");
        assertThrows(UnexpectedCharacterException.class, parserState::object);
    }

    @Test
    @DisplayName("object() should return correct result for object with one member")
    void testParseObjectWithOneMember() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{\"key\":1234}");
        JsonElement.JsonObject result = parserState.object();
        assertThat((JsonElement.JsonNumber) result.get("key")).isEqualByComparingTo(new JsonElement.JsonNumber("1234"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should return correct result for object with multiple members")
    void testParseObjectWithMultipleMembers() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{\"key1\":1234,\"key2\":\"string\",\"key3\":null}");
        JsonElement.JsonObject result = parserState.object();
        assertThat((JsonElement.JsonNumber) result.get("key1")).isEqualByComparingTo(new JsonElement.JsonNumber("1234"));
        assertThat(result.get("key2")).isEqualTo(new JsonElement.JsonString("string"));
        assertThat(result.get("key3")).isEqualTo(JsonElement.NULL);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should return correct result for object with multiple members ignoring whitespace")
    void testParseObjectWithMultipleMembersIgnoringWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{ \"key1\" : 1234 , \"key2\" : \"string\" , \"key3\" : null }");
        JsonElement.JsonObject result = parserState.object();
        assertThat((JsonElement.JsonNumber) result.get("key1")).isEqualByComparingTo(new JsonElement.JsonNumber("1234"));
        assertThat(result.get("key2")).isEqualTo(new JsonElement.JsonString("string"));
        assertThat(result.get("key3")).isEqualTo(JsonElement.NULL);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("array() should return empty Array for empty input")
    void testParseEmptyArray() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[]");
        JsonElement.JsonArray result = parserState.array();
        assertThat(result.elements()).isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("array() should return empty Array for empty input with whitespace")
    void testParseEmptyArrayWithWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[ \n]");
        JsonElement.JsonArray result = parserState.array();
        assertThat(result.elements()).isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("array() should throw exception if not starting with opening brackets")
    void testParseEmptyArrayWithoutStartingBrackets() {
        JsonParseState parserState = new JsonParseState(" ]");
        assertThrows(UnexpectedCharacterException.class, parserState::array);
    }

    @Test
    @DisplayName("array() should throw exception if not ending with closing brackets")
    void testParseEmptyArrayWithoutEndingBrackets() {
        JsonParseState parserState = new JsonParseState("[ ");
        assertThrows(UnexpectedCharacterException.class, parserState::array);
    }

    @Test
    @DisplayName("array() should return correct result for array with one element")
    void testParseArrayWithOneElement() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[\"string\"]");
        JsonElement.JsonArray result = parserState.array();
        assertThat(result.elements()).hasSize(1);
        assertThat(result.get(0)).isEqualTo(new JsonElement.JsonString("string"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("array() should return correct result for array with multiple elements")
    void testParseArrayWithMultipleElements() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[\"string1\",\"string2\",\"string3\"]");
        JsonElement.JsonArray result = parserState.array();
        assertThat(result.elements()).hasSize(3);
        assertThat(result.get(0)).isEqualTo(new JsonElement.JsonString("string1"));
        assertThat(result.get(1)).isEqualTo(new JsonElement.JsonString("string2"));
        assertThat(result.get(2)).isEqualTo(new JsonElement.JsonString("string3"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("array() should return correct result for array with multiple elements and whitespace")
    void testParseArrayWithMultipleElementsAndWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[ \"string1\" , \"string2\" , \"string3\" ]");
        JsonElement.JsonArray result = parserState.array();
        assertThat(result.elements()).hasSize(3);
        assertThat(result.get(0)).isEqualTo(new JsonElement.JsonString("string1"));
        assertThat(result.get(1)).isEqualTo(new JsonElement.JsonString("string2"));
        assertThat(result.get(2)).isEqualTo(new JsonElement.JsonString("string3"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return null for null input")
    void testParseNullValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("null");
        JsonElement result = parserState.value();
        assertThat(result).isEqualTo(JsonElement.NULL);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return boolean true for boolean input with value true")
    void testParseBooleanValueTrue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("true");
        JsonElement result = parserState.value();
        assertThat(result).isEqualTo(JsonElement.JsonBoolean.TRUE);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return boolean false for boolean input with value false")
    void testParseBooleanValueFalse() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("false");
        JsonElement result = parserState.value();
        assertThat(result).isEqualTo(JsonElement.JsonBoolean.FALSE);
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return integer for integer input")
    void testParseIntegerValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("2345");
        JsonElement result = parserState.value();
        assertThat(result).isInstanceOf(JsonElement.JsonNumber.class);
        assertThat((JsonElement.JsonNumber) result).isEqualByComparingTo(new JsonElement.JsonNumber("2345"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return integer for negative integer input")
    void testParseNegativeIntegerValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("-2345");
        JsonElement result = parserState.value();
        assertThat(result).isInstanceOf(JsonElement.JsonNumber.class);
        assertThat((JsonElement.JsonNumber) result).isEqualByComparingTo(new JsonElement.JsonNumber("-2345"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return unquoted string for quoted string input")
    void testParseStringValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("\"value\"");
        JsonElement result = parserState.value();
        assertThat(result).isEqualTo(new JsonElement.JsonString("value"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return Map for object input")
    void testParseObjectValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("{}");
        JsonElement result = parserState.value();
        assertThat(result)
                .isInstanceOf(JsonElement.JsonObject.class)
                .extracting(jsonElement -> ((JsonElement.JsonObject) jsonElement).members())
                .asInstanceOf(InstanceOfAssertFactories.MAP)
                .isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should return Array for array input")
    void testParseArrayValue() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[]");
        JsonElement result = parserState.value();
        assertThat(result)
                .isInstanceOf(JsonElement.JsonArray.class)
                .extracting(jsonElement -> ((JsonElement.JsonArray) jsonElement).elements())
                .asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("value() should throw JsonParseException for unexpected input")
    void testValueUnexpectedInput() {
        JsonParseState parserState = new JsonParseState("a");
        assertThrows(JsonParseException.class, parserState::value);
    }

    @Test
    @DisplayName("element() should return value ignoring leading whitespace")
    void testElementLeadingWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState(" []");
        JsonElement result = parserState.element();
        assertThat(result)
                .isInstanceOf(JsonElement.JsonArray.class)
                .extracting(jsonElement -> ((JsonElement.JsonArray) jsonElement).elements())
                .asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("element() should return value ignoring trailing whitespace")
    void testElementTrailingWhitespace() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("[]  ");
        JsonElement result = parserState.element();
        assertThat(result)
                .isInstanceOf(JsonElement.JsonArray.class)
                .extracting(jsonElement -> ((JsonElement.JsonArray) jsonElement).elements())
                .asInstanceOf(InstanceOfAssertFactories.ITERABLE)
                .isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("json() should return element")
    void testJson() throws JsonParseException {
        JsonParseState parserState = new JsonParseState("  \"test\"  ");
        JsonElement result = parserState.json();
        assertThat(result).isEqualTo(new JsonElement.JsonString("test"));
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("json() should throw exception if there are remaining characters")
    void testJsonRemainingCharacters() {
        JsonParseState parserState = new JsonParseState("  \"test\" \" ");
        assertThrows(JsonParseException.class, parserState::json);
    }

    @Test
    @DisplayName("json() should throw exception if the input string is empty")
    void testJsonEmptyInput() {
        JsonParseState parserState = new JsonParseState("");
        assertThrows(JsonParseException.class, parserState::json);
    }

    @Test
    @DisplayName("array() should throw exception when missing closing bracket after comma")
    void testArrayMissingCloseAfterComma() {
        JsonParseState jsonParseState = new JsonParseState("[\"a\",");
        assertThrows(JsonParseException.class, jsonParseState::array);
    }

    @Test
    @DisplayName("object() should throw exception when missing closing bracket after comma")
    void testObjectMissingCloseAfterComma() {
        JsonParseState jsonParseState = new JsonParseState("{\"a\",");
        assertThrows(JsonParseException.class, jsonParseState::object);
    }

    @Test
    @DisplayName("number() should throw exception when starting with a +")
    void testNumberWithPlus() {
        JsonParseState jsonParseState = new JsonParseState("+5");
        assertThrows(JsonParseException.class, jsonParseState::number);
    }

    @Test
    @DisplayName("number() should throw exception when encountering non-latin digit")
    void testNumberNonLatinDigit() {
        JsonParseState jsonParseState = new JsonParseState("\uff11");
        assertThrows(JsonParseException.class, jsonParseState::number);
    }

    @Test
    @DisplayName("string() should throw exception upon unfinished \\u")
    void testStringUnfinishedUnicode() {
        JsonParseState jsonParseState = new JsonParseState("\"\\u");
        assertThrows(JsonParseException.class, jsonParseState::string);
    }

    @Test
    @DisplayName("string() should throw exception upon non-hex \\u")
    void testStringNonHexUnicode() {
        JsonParseState jsonParseState = new JsonParseState("\"\\utttt\"");
        assertThrows(UnexpectedCharacterException.class, jsonParseState::string);
    }

    @ParameterizedTest
    @DisplayName("string() should throw exception upon illegal characters")
    @MethodSource("provideInputForTestStringIllegalCharacter")
    void testStringIllegalCharacter(String charToTest) {
        JsonParseState jsonParseState = new JsonParseState("\"" + charToTest + "\"");
        assertThrows(UnexpectedCharacterException.class, jsonParseState::string);
    }

    @Test
    @DisplayName("string() should throw no exception legal characters")
    void testStringLegalCharacter() {
        JsonParseState jsonParseState = new JsonParseState("\"\u0020\"");
        assertDoesNotThrow(jsonParseState::string);
    }

    @Test
    @DisplayName("current() should throw Error when invoked on an empty String")
    void testCurrentEmptyString() {
        JsonParseState jsonParseState = new JsonParseState("");
        assertThrows(AssertionError.class, jsonParseState::current);
    }

    private static Stream<Arguments> provideInputForTestParseStringWithEscapedControlCharacters() {
        return Stream.of(
                Arguments.of("\\/", "/"),
                Arguments.of("\\b", "\b"),
                Arguments.of("\\f", "\f"),
                Arguments.of("\\n", "\n"),
                Arguments.of("\\r", "\r"),
                Arguments.of("\\t", "\t")
        );
    }

    private static Stream<String> provideInputForTestStringIllegalCharacter() {
        return Stream.of("\u0000", "\n", "\t", "a\u0019");
    }

}