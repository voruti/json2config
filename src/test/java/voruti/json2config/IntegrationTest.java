package voruti.json2config;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegrationTest {

    private static final String RESOURCES = "build/resources/test/";
    private static final String TEMPORARY = "build/tmp/test/";


    private String openFile(String file) throws IOException {
        Path path = Paths.get(file);
        return String.join("\n", Files.readAllLines(path))
                .replaceAll("[\\h\\t ]{2,}", " ");
    }


    @ParameterizedTest
    @ValueSource(strings = {"openhab2_example1", "openhab2_multipleChannelsOneItem"})
    void itemsAndChannels(String testName) throws IOException {
        // arrange:
        final String generatedItemsFile = TEMPORARY + testName + "_result.items";

        // load template .items file:
        String template = openFile(RESOURCES + testName + ".items");

        // act:
        Starter.main(new String[]{"-i", RESOURCES + testName + ".Item.json",
                "-o", generatedItemsFile,
                "-c",
                "-m",
                "-d", TEMPORARY,
                "--channel-file", RESOURCES + testName + ".ItemChannelLink.json",
                "--metadata-file", RESOURCES + testName + ".Metadata.json"});

        // assert:
        // load generated .items file:
        String generated = openFile(generatedItemsFile);

        assertEquals(template, generated);
    }

    @ParameterizedTest
    @ValueSource(strings = {"openhab2_exampleDimensions"})
    void onlyItems(String testName) throws IOException {
        // arrange:
        final String generatedItemsFile = TEMPORARY + testName + "_result.items";

        // load template .items file:
        String template = openFile(RESOURCES + testName + ".items");

        // act:
        Starter.main(new String[]{"-i", RESOURCES + testName + ".Item.json",
                "-o", generatedItemsFile});

        // assert:
        // load generated .items file:
        String generated = openFile(generatedItemsFile);

        assertEquals(template, generated);
    }
}
