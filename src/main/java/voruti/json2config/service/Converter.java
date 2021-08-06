package voruti.json2config.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import voruti.json2config.model.IConvertible;
import voruti.json2config.model.json.JsonChannelLink;
import voruti.json2config.model.json.JsonItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * @author voruti
 */
@Slf4j
public class Converter {

    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");
    private static final Gson GSON = new GsonBuilder().create();


    private Converter() {
    }


    /**
     * Converts {@code jsonFile} to {@code outputFile}.
     *
     * @param jsonFile   path to file (input)
     * @param outputFile path to file (output)
     * @param type       type of file to convert
     */
    public static void start(String jsonFile, String outputFile, Type type) {
        log.info("Starting Converter with jsonFile={}, outputFile={}, type={}", jsonFile, outputFile, type);

        try {
            // open file:
            String content = openFileToString(jsonFile);
            // map into map:
            Map<String, IConvertible> convertibleMap = openFileToConvertibleMap(content, type);
            // get lines from map:
            List<String> lines = convertibleMapToLines(convertibleMap);
            // write file:
            writeLinesToFile(lines, outputFile);
        } catch (FileNotFoundException e) {
            log.error("File {} not found", jsonFile);
        }
    }

    /**
     * Open the file with {@code fileName} and return its content as {@link String}.
     *
     * @param fileName the path/name of the file to open
     * @return a {@link String} with the content
     * @throws FileNotFoundException if the file can't be found
     */
    public static String openFileToString(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        StringBuilder str = new StringBuilder();
        Scanner sc = new Scanner(file);

        log.info("Reading lines of file={} with Scanner={}", file, sc);
        while (sc.hasNextLine()) {
            str.append(sc.nextLine());
        }
        sc.close();

        return str.toString();
    }

    /**
     * Convert the {@code json} with {@link Gson} to a {@link Map}.
     *
     * @param json a {@link String} which contains JSON
     * @param type the {@link Type} of the content in the {@code json}
     * @return a {@link Map} with {@link String} as key and {@link IConvertible} as value
     */
    public static Map<String, IConvertible> openFileToConvertibleMap(String json, Type type) {
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
     * Add entries of {@link JSONArray} {@code val} to {@code elementList}
     *
     * @param elementList the {@link StringJoiner} or {@link Collection} to which to add the elements
     * @param key         used for logging
     * @param val         a {@link JSONArray}
     */
    public static void mapArray(Object elementList, String key, Object val) {
        if (val instanceof JSONArray) {
            for (Object o : (JSONArray) val) {
                if (o instanceof String) {
                    String string = (String) o;
                    if (elementList instanceof StringJoiner) {
                        ((StringJoiner) elementList).add(string);
                    } else {
                        //noinspection unchecked
                        ((Collection<String>) elementList).add(string);
                    }
                } else {
                    log.warn("JSONArray={} item={} is not instanceof String!", key, o);
                }
            }
        } else {
            log.warn("{}={} is not instanceof JSONArray!", key, val);
        }
    }

    /**
     * Converts the {@code map} with objects of {@link IConvertible} implementing
     * classes into {@link String} lines in form of a {@link List}.
     *
     * @param map the map to convert
     * @return a {@link List} with all lines as {@link String Strings}
     */
    public static List<String> convertibleMapToLines(Map<String, IConvertible> map) {
        List<String> newLines = new ArrayList<>();

        if (map.size() > 0) {
            List<String> lines = new ArrayList<>();

            for (Entry<String, IConvertible> entry : map.entrySet()) {
                log.trace("Generating line for {}: {}", entry.getKey(), entry.getValue());
                String line = entry.getValue().toConfigLine(entry.getKey());
                log.info("Created line=[{}]", line);
                lines.add(line);
            }

            lines.sort(Comparator.naturalOrder());

            // adding empty lines between:
            String last = lines.get(0).substring(0, 4);
            for (String line : lines) {
                String now = line.substring(0, 4);
                if (!now.equalsIgnoreCase(last)) {
                    newLines.add("");
                }
                newLines.add(line);

                last = now;
            }
        } else {
            log.warn("No objects in map={}", map);
        }

        return newLines;
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
