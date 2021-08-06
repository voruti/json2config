package voruti.json2config;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegrationTest {

    @Test
    void openhab2_example1() throws IOException, URISyntaxException {
        // arrange:
        // load template .items file:
        Path pathTemplate = Paths.get(ClassLoader.getSystemResource("json.items").toURI());
        String template = String.join("\n", Files.readAllLines(pathTemplate))
                .replaceAll("[\\h\\t ]{2,}", " ");

        // act:
        Starter.main(new String[]{"-i", "build/resources/test/org.eclipse.smarthome.core.items.Item.json",
                "-o", "build/tmp/test/junit.items",
                "-c",
                "-d", "build/tmp/test/",
                "--channel-file", "build/resources/test/org.eclipse.smarthome.core.thing.link.ItemChannelLink.json"});

        // assert:
        // load generated .items file:
        Path pathGenerated = Paths.get("build/tmp/test/junit.items");
        String generated = String.join("\n", Files.readAllLines(pathGenerated))
                .replaceAll("[\\h\\t ]{2,}", " ");

        assertEquals(template, generated);
    }
}
