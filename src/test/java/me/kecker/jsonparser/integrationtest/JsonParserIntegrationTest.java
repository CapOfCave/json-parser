package me.kecker.jsonparser.integrationtest;

import me.kecker.jsonparser.JsonParser;
import me.kecker.jsonparser.exceptions.JsonParseException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonParserIntegrationTest {

    @Disabled
    @ParameterizedTest
    @MethodSource("fileNameProvider")
    void testExampleFiles(String fileName) {
        String expectedResult = fileName.substring(0, 1);
        String input = ResourceLoader.loadFile(fileName);
        switch (expectedResult) {
            case "y" -> assertDoesNotThrow(() -> JsonParser.parse(input));
            case "n" -> assertThrows(JsonParseException.class, () -> JsonParser.parse(input));
            // case "i" -> do nothing;
        }
    }

    private static Stream<String> fileNameProvider() {
        return ResourceLoader.streamTestCaseNames();
    }

}
