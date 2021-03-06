package me.kecker.jsonparser.integrationtest;

import me.kecker.jsonparser.JsonParser;
import me.kecker.jsonparser.exceptions.JsonParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserIntegrationTest {

    @ParameterizedTest
    @MethodSource("fileNamesToAcceptProvider")
    void testExampleFileToAccept(String fileName) {
        String input = ResourceLoader.loadFile(fileName);
        assertDoesNotThrow(() -> JsonParser.parse(input));
    }

    @ParameterizedTest
    @MethodSource("fileNamesToRejectProvider")
    void testExampleFilesToReject(String fileName) {
        String input = ResourceLoader.loadFile(fileName);
        assertThrows(JsonParseException.class, () -> JsonParser.parse(input));
    }

    @ParameterizedTest
    @MethodSource("indifferentFileNamesProvider")
    void testIndifferentExampleFiles(String fileName) {
        String input = ResourceLoader.loadFile(fileName);
        try {
            JsonParser.parse(input);
        } catch (JsonParseException e) {
            // JsonParseExceptions are allowed as the result does not matter
        }
    }

    private static Stream<String> fileNamesToAcceptProvider() {
        return getFilteredExampleData("y");
    }

    private static Stream<String> fileNamesToRejectProvider() {
        return getFilteredExampleData("n");
    }

    private static Stream<String> indifferentFileNamesProvider() {
        return getFilteredExampleData("i");
    }

    private static Stream<String> getFilteredExampleData(String y) {
        return ResourceLoader.streamTestCaseNames().filter(s -> s.startsWith(y));
    }
}
