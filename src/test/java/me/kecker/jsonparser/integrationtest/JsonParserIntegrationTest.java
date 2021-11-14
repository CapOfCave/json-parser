package me.kecker.jsonparser.integrationtest;

import me.kecker.jsonparser.JsonParser;
import me.kecker.jsonparser.exceptions.JsonParseException;
import org.junit.jupiter.api.Disabled;
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
        System.out.println(fileName);
        assertDoesNotThrow(() -> JsonParser.parse(input));
    }

    @Disabled
    @ParameterizedTest
    @MethodSource("fileNamesToRejectProvider")
    void testExampleFilesToReject(String fileName) {
        String input = ResourceLoader.loadFile(fileName);
        System.out.println(fileName);
        assertThrows(JsonParseException.class, () -> JsonParser.parse(input));
    }

    private static Stream<String> fileNamesToAcceptProvider() {
        return ResourceLoader.streamTestCaseNames().filter(s -> s.startsWith("y"));
    }

    private static Stream<String> fileNamesToRejectProvider() {
        return ResourceLoader.streamTestCaseNames().filter(s -> s.startsWith("n"));
    }
}
