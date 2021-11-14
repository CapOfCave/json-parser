package me.kecker.jsonparser;

import me.kecker.jsonparser.exceptions.IllegalTokenException;
import me.kecker.jsonparser.exceptions.UnexpectedCharacterException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("bool() should return true for input 'true'")
    void testBooleanTrue() throws IllegalTokenException {
        JsonParseState parserState = new JsonParseState("true");
        boolean result = parserState.bool();
        assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("bool() should return false for input 'false'")
    void testBooleanFalse() throws IllegalTokenException {
        JsonParseState parserState = new JsonParseState("false");
        boolean result = parserState.bool();
        assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("bool() should throw exception for any non-boolean input")
    void testBooleanOtherInput() {
        JsonParseState parserState = new JsonParseState("other");
        assertThrows(IllegalTokenException.class, parserState::bool);
    }

    @Test
    @DisplayName("string() should return the input string without quotes")
    void testParseString() throws UnexpectedCharacterException {
        JsonParseState parserState = new JsonParseState("\"input\"");
        String result = parserState.string();
        assertThat(result).isEqualTo("input");
    }

    @Test
    @DisplayName("string() should throw exception if not starting with quotes")
    void testParseStringWithoutLeadingQuote() {
        JsonParseState parserState = new JsonParseState("input\"");
        assertThrows(UnexpectedCharacterException.class, parserState::string);
    }

    @Test
    @DisplayName("object() should return empty Map for empty object")
    void testParseEmptyObject() throws UnexpectedCharacterException {
        JsonParseState parserState = new JsonParseState("{}");
        Map<String, Object> result = parserState.object();
        assertThat(result).isEmpty();
        assertThat(parserState.reachedEnd()).isEqualTo(true);
    }

    @Test
    @DisplayName("object() should return empty Map for empty object with whitespace")
    void testParseEmptyObjectWithWhitespace() throws UnexpectedCharacterException {
        JsonParseState parserState = new JsonParseState("{ \n}");
        Map<String, Object> result = parserState.object();
        assertThat(result).isEmpty();
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

}