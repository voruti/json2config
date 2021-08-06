package voruti.json2config.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import voruti.json2config.model.IConvertible;
import voruti.json2config.model.json.JsonChannelLink;
import voruti.json2config.model.json.JsonItem;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
public class SharedService {

    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");
    private static final Gson GSON = new GsonBuilder().create();


    private SharedService() {
    }


    /**
     * Open the file with {@code fileName} and return its content as {@link String}.
     *
     * @param fileName the path/name of the file to open
     * @return a {@link String} with the content
     * @throws IOException if the file can't be opened
     */
    public static String openFileToString(String fileName) throws IOException {
        Path path = Paths.get(fileName);
        log.info("Reading lines at path={}", path);
        return String.join("\n", Files.readAllLines(path, Charset.defaultCharset()));
    }

    /**
     * Convert the {@code json} with {@link Gson} to a {@link Map}.
     *
     * @param json a {@link String} which contains JSON
     * @param type the {@link Type} of the content in the {@code json}
     * @return a {@link Map} with {@link String} as key and {@link IConvertible} as value
     */
    public static Map<String, IConvertible> jsonToConvertibleMap(String json, Type type) {
        java.lang.reflect.Type mapType = null;
        switch (type) {
            case ITEM:
                mapType = new TypeToken<Map<String, JsonItem>>() {
                }.getType();
                break;
            case THING:
                // mapType =  new TypeToken<Map<String, JsonThing>>() {}.getType();
                break;
            case CHANNEL:
                mapType = new TypeToken<Map<String, JsonChannelLink>>() {
                }.getType();
                break;

            default:
                log.error(FATAL, "Wrong type={}", type);
                break;
        }

        return GSON.fromJson(json, mapType);
    }

    /**
     * Writes every entry of {@code lines} in a separate line to {@code fileName}.
     *
     * @param lines    the lines to write into the file
     * @param fileName the file name of the file to write
     * @return {@code true} if the writing operation was successful, {@code false}
     * otherwise
     */
    public static boolean writeLinesToFile(List<String> lines, String fileName) {
        boolean returnVal;

        if (!lines.isEmpty()) {
            // writing to file:
            try {
                log.info("Writing lines to file={}", fileName);
                Files.write(Paths.get(fileName), lines, Charset.defaultCharset());
                returnVal = true;
            } catch (IOException e) {
                log.error(FATAL, "{} at writing file with lines={}", e, lines);
                e.printStackTrace();
                returnVal = false;
            }
        } else {
            log.warn("No objects in List lines={}", lines);
            returnVal = false;
        }

        return returnVal;
    }

    public enum Type {
        ITEM, THING, CHANNEL
    }
}
