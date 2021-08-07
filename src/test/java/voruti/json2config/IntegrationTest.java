package voruti.json2config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegrationTest {

    private static final String RESOURCES = "build/resources/test/";
    private static final String TEMPORARY = "build/tmp/test/";

    @Test
    void openhab2_example1() throws IOException {
        // arrange:
        final String testName = "openhab2_example1";
        final String generatedItemsFile = TEMPORARY + testName + "_result.items";

        // load template .items file:
        Path pathTemplate = Paths.get(RESOURCES + testName + ".items");
        String template = String.join("\n", Files.readAllLines(pathTemplate))
                .replaceAll("[\\h\\t ]{2,}", " ");

        // act:
        Starter.main(new String[]{"-i", RESOURCES + testName + ".Item.json",
                "-o", generatedItemsFile,
                "-c",
                "-d", TEMPORARY,
                "--channel-file", RESOURCES + testName + ".ItemChannelLink.json"});

        // assert:
        // load generated .items file:
        Path pathGenerated = Paths.get(generatedItemsFile);
        String generated = String.join("\n", Files.readAllLines(pathGenerated))
                .replaceAll("[\\h\\t ]{2,}", " ");

        assertEquals(template, generated);
    }

    @Test
    void openhab2_exampleDimensions() throws IOException {
        // arrange:
        final String testName = "openhab2_exampleDimensions";
        final String generatedItemsFile = TEMPORARY + testName + "_result.items";

        // load template .items file:
        Path pathTemplate = Paths.get(RESOURCES + testName + ".items");
        String template = String.join("\n", Files.readAllLines(pathTemplate))
                .replaceAll("[\\h\\t ]{2,}", " ");

        // act:
        Starter.main(new String[]{"-i", RESOURCES + testName + ".Item.json",
                "-o", generatedItemsFile});

        // assert:
        // load generated .items file:
        Path pathGenerated = Paths.get(generatedItemsFile);
        String generated = String.join("\n", Files.readAllLines(pathGenerated))
                .replaceAll("[\\h\\t ]{2,}", " ");

        assertEquals(template, generated);
    }
}
