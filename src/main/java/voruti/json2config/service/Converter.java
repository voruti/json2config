package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.model.IConvertible;
import voruti.json2config.service.SharedService.Type;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author voruti
 */
@Slf4j
public class Converter {

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
            String content = SharedService.openFileToString(jsonFile);
            // map into map:
            Map<String, IConvertible> convertibleMap = SharedService.jsonToConvertibleMap(content, type);
            // get lines from map:
            List<String> lines = convertibleMapToLines(convertibleMap);
            // write file:
            SharedService.writeLinesToFile(lines, outputFile);
        } catch (IOException e) {
            log.error("Can't open file {}", jsonFile);
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
        List<String> lines = map.entrySet().stream()
                .map(entry -> entry.getValue().toConfigLine(entry.getKey()))
                .sorted()
                .collect(Collectors.toList());

        // adding empty lines between:
        String last = lines.get(0).substring(0, 4);
        for (int i = 1; i < lines.size(); i++) {
            String now = lines.get(i).substring(0, 4);

            if (!now.equals(last)) {
                lines.add(i, "");
            }

            last = now;
        }

        return lines;
    }
}
