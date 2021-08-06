package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import voruti.json2config.model.IConvertible;
import voruti.json2config.model.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author voruti
 */
@Slf4j
public class Converter {

    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");


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

        // get the jsonObject:
        JSONObject jsonObject = openFileToJSONObject(jsonFile);
        // convert first elements to map of IConvertibles:
        Map<String, IConvertible> convertibleMap = goThroughFirstEntriesOfJSONObject(jsonObject, type);
        // get lines from map:
        List<String> lines = convertibleMapToLines(convertibleMap);
        // write file:
        writeLinesToFile(lines, outputFile);
    }

    /**
     * Opens and reads file {@code fileName} and returns it content as
     * {@link JSONObject}.
     *
     * @param fileName the file to open and read
     * @return the {@link JSONObject} containing the content of the file, if it
     * could be successfully opened, otherwise {@code null}
     */
    public static JSONObject openFileToJSONObject(String fileName) {
        JSONObject jsonObject = null;

        File file = new File(fileName);
        StringBuilder str = new StringBuilder();
        Scanner sc;
        try {
            sc = new Scanner(file);

            log.info("Reading lines of file={} with Scanner={}", file, sc);
            while (sc.hasNextLine()) {
                str.append(sc.nextLine());
            }
            sc.close();

            jsonObject = new JSONObject(str.toString());
        } catch (JSONException eJ) {
            log.error(FATAL, "File content={} can not be parsed to JSONObject", str);
            eJ.printStackTrace();
        } catch (FileNotFoundException eF) {
            log.error(FATAL, "file={} can not be opened!", file);
            eF.printStackTrace();
        }

        return jsonObject;
    }

    /**
     * Creates a {@link Map} of all "first values" of the {@code jsonObject}. Uses
     * {@code type} to determine in which {@link Type} to convert the
     * {@code jsonObject} entries.
     *
     * @param jsonObject the {@link JSONObject} to convert
     * @param type       the {@link Type} in which to convert the {@code jsonObject}
     *                   entries
     * @return a {@link Map} containing the {@link IConvertible IConvertibles} of
     * the {@code jsonObject}; contains no entries if {@code jsonObject} is
     * {@code null} (or wrong {@code type} is found)
     */
    public static Map<String, IConvertible> goThroughFirstEntriesOfJSONObject(JSONObject jsonObject, Type type) {
        Map<String, IConvertible> returnVal = new HashMap<>();

        if (jsonObject != null) {
            Iterator<String> ite = jsonObject.keys();
            loopW:
            while (ite.hasNext()) {
                String key = ite.next();

                Object o = jsonObject.get(key);
                if (!(o instanceof JSONObject)) {
                    log.error(FATAL, "Value ({}) should be instanceof JSONObject, but is not!", o);
                    break;
                }
                JSONObject val = (JSONObject) o;

                IConvertible iconv = null;
                switch (type) {
                    case ITEM:
                        iconv = createItem(val);
                        break;
                    case THING:
                        // iconv = createThing(val);
                        break;
                    case CHANNEL:
                        iconv = ChannelAppender.createChannel(val);
                        break;

                    default:
                        log.error(FATAL, "Wrong type={}", type);
                        break loopW;
                }

                log.info("Adding IConvertible={} to convertiblesMap", iconv);
                returnVal.put(key, iconv);
            }
        } else {
            log.warn("jsonObject is null");
        }

        return returnVal;
    }

    /**
     * Creates a {@link Item} out of a {@link JSONObject}.
     *
     * @param content the {@link JSONObject}
     * @return the item as {@link Item}
     */
    public static Item createItem(JSONObject content) {
        String itemType = "";
        String label = "";
        String category = "";
        String baseItemType = "";
        String functionName = "";
        List<String> groupNames = new ArrayList<>();
        Set<String> tags = new HashSet<>();
        List<String> functionParams = new ArrayList<>();
        String dimension = "";

        Iterator<String> ite1 = content.keys();
        while (ite1.hasNext()) {
            String key1 = ite1.next();
            Object val1 = content.get(key1);

            switch (key1) {
                case "class":
                    if (!val1.equals("org.eclipse.smarthome.core.items.ManagedItemProvider$PersistedItem")) {
                        log.warn("class={} different than expected!", val1);
                    }
                    break;
                case "value":
                    if (val1 instanceof JSONObject) {
                        JSONObject jso2 = (JSONObject) val1;
                        Iterator<String> ite2 = jso2.keys();
                        while (ite2.hasNext()) {
                            String key2 = ite2.next();
                            Object val2 = jso2.get(key2);

                            switch (key2) {
                                case "itemType":
                                    if (val2 instanceof String) {
                                        itemType = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;
                                case "label":
                                    if (val2 instanceof String) {
                                        label = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;
                                case "category":
                                    if (val2 instanceof String) {
                                        category = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;
                                case "baseItemType":
                                    if (val2 instanceof String) {
                                        baseItemType = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;
                                case "functionName":
                                    if (val2 instanceof String) {
                                        functionName = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;
                                case "groupNames":
                                    mapArray(groupNames, key2, val2);
                                    break;
                                case "tags":
                                    mapArray(tags, key2, val2);
                                    break;
                                case "functionParams":
                                    mapArray(functionParams, key2, val2);
                                    break;
                                case "dimension":
                                    if (val2 instanceof String) {
                                        dimension = (String) val2;
                                    } else {
                                        log.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;

                                default:
                                    log.warn("Unexpected key={}", key2);
                                    break;
                            }
                        }
                        break;
                    } else {
                        log.warn("{}={} is not instanceof JSONObject!", key1, val1);
                    }
                    break;

                default:
                    log.warn("Unexpected key={}", key1);
                    break;
            }
        }

        return new Item(baseItemType, groupNames, itemType, tags, label, category, functionName, functionParams, dimension);
    }

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

            for (Map.Entry<String, IConvertible> entry : map.entrySet()) {
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
