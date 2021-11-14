package me.kecker.jsonparser.integrationtest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ResourceLoader {
    public static Stream<String> streamTestCaseNames() {
        return streamFile("tests.txt");
    }

    public static String loadFile(String fileName) {
        return streamFile(fileName).collect(Collectors.joining("\n"));
    }

    private static Stream<String> streamFile(String fileName) {
        InputStream inputStream = ResourceLoader.class.getResourceAsStream(fileName);
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines();
    }
}
