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
    void example1() throws IOException, URISyntaxException {
        // arrange:
        // create "out" directory:
        Path path = Paths.get("target/test-classes/out/");
        Files.createDirectories(path);

        // load template .items file:
        Path pathTemplate = Paths.get(ClassLoader.getSystemResource("json.items").toURI());
        String template = String.join(System.lineSeparator(), Files.readAllLines(pathTemplate));

        // act:
        Starter.main(new String[]{"-i", "target/test-classes/org.eclipse.smarthome.core.items.Item.json",
                "-o", "target/test-classes/out/junit.items",
                "-c",
                "-d", "target/test-classes/out/",
                "--channel-file", "target/test-classes/org.eclipse.smarthome.core.thing.link.ItemChannelLink.json"});

        // assert:
        // load generated .items file:
        Path pathGenerated = Paths.get(ClassLoader.getSystemResource("out/junit.items").toURI());
        String generated = String.join(System.lineSeparator(), Files.readAllLines(pathGenerated));

        assertEquals(template, generated);
    }
}
