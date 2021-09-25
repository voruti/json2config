package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.model.IAppendable;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Common code for appending channel links and metadata to items
 *
 * @author sbholmes
 */
@Slf4j
public class Appender {

    private Appender() {
    }
    

    /**
     * Appends data found in {@code appendableList} onto the end of items in the {@code directory}
     *
     * @param directory      the directory in which to search for ".items" files
     * @param appendableList the list of data that needs appending to items
     */
    public static void searchAndAppend(String directory, List<IAppendable> appendableList) {
        // search items files:
        List<String> itemsFiles = Appender.findItemsFilesInDir(directory);
        // get names of all items:
        List<String> itemNamesList = itemsFiles.stream()
                .map(Appender::getItemNamesFromFile)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("Found {} items", itemNamesList.size());
        log.trace("itemNamesList={}", itemNamesList);

        // only data present in both lists:
        List<IAppendable> relevantDataList = appendableList.stream()
                .filter(data -> itemNamesList.stream()
                        .anyMatch(itemName -> itemName.equals(data.getItemName())))
                .collect(Collectors.toList());
        log.info("{} match with each other", relevantDataList.size());
        log.trace("relevantDataList={}", relevantDataList);

        // append the right data to the right item
        int count = 0;
        for (IAppendable appendable : relevantDataList) {
            for (String iFile : itemsFiles) {
                if (Appender.appendToItemInFile(appendable, iFile)) {
                    count++;
                }
            }
        }
        log.info("Successfully appended {} channels/metadata!", count);

        log.warn("Warning: You might need to manually fix some converting mistakes (double channels, etc.)");
    }

    /**
     * Returns a {@link List} of Strings containing the names of all items in
     * {@code fileName}.
     *
     * @param fileName the file(-Name) to open and search for items
     * @return a {@link List} containing the names of the items
     */
    public static List<String> getItemNamesFromFile(String fileName) {
        try {
            return Arrays.stream(SharedService.openFileToString(fileName).split("\n"))
                    .filter(line -> !line.isEmpty())
                    .map(Appender::searchNameInLine)
                    .filter(itemName -> !itemName.isEmpty())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error(Constants.LOG_CANT_OPEN_FILE, fileName);
        }

        return List.of();
    }

    /**
     * Appends the {@code appendable} after the item in {@code fileName}.
     *
     * @param appendable the data to append after the item
     * @param fileName   the file to search for the item
     * @return {@code true} if the {@code appendable} could be appended, {@code false} otherwise
     */
    public static boolean appendToItemInFile(IAppendable appendable, String fileName) {
        boolean successful = false;

        try {
            List<String> originalLines = Arrays.asList(SharedService.openFileToString(fileName).split("\n"));
            List<String> modifiedLines = originalLines.stream()
                    .map(line -> {
                        if (!line.isEmpty()) {
                            String readItemName = searchNameInLine(line);
                            if (!readItemName.isEmpty() && readItemName.equals(appendable.getItemName())) {
                                return appendable.toConfigLine(line);
                            }
                        }
                        // return unmodified line:
                        return line;
                    })
                    .collect(Collectors.toList());

            if (!originalLines.equals(modifiedLines)) {
                successful = SharedService.writeLinesToFile(modifiedLines, fileName);
            }
        } catch (IOException e) {
            log.error(Constants.LOG_CANT_OPEN_FILE, fileName);
        }

        return successful;
    }

    /**
     * Searches for an item name in the {@code line}.
     *
     * @param line the line to search in
     * @return name of the item if found, {@code null} otherwise
     */
    public static String searchNameInLine(String line) {
        String[] arr = line.strip().split("\\s+");

        String itemName = "";
        if (arr.length >= 2) {
            itemName = arr[1];
        }

        return itemName;
    }

    /**
     * Searches for ".items" files in the {@code directory}.
     *
     * @param directory the directory to search in
     * @return a {@link List} with all file paths of ".items" files
     */
    public static List<String> findItemsFilesInDir(String directory) {
        return Arrays.stream(Objects.requireNonNull(new File(directory).listFiles((dir, filename) -> filename.endsWith(".items"))))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }
}
