package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import voruti.json2config.model.json.JsonChannelLink;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * @author voruti
 */
@Slf4j
public class ChannelAppender {

    private static final Marker FATAL = MarkerFactory.getMarker("FATAL");


    private ChannelAppender() {
    }


    /**
     * Appends the channel links from {@code channelLinkFile} to all ".items" files
     * in the {@code directory}.
     *
     * @param channelLinkFile path to the file which contains the channel links in
     *                        JSON format
     * @param directory       the directory in which to search for ".items" files
     */
    public static void start(String channelLinkFile, String directory) {
        log.info("Starting ChannelAppender with channelLinkFile={}, directory={}", channelLinkFile, directory);

        try {
            // open file:
            String content = Converter.openFileToString(channelLinkFile);
            // map to list of channel links:
            List<JsonChannelLink> channelsList = Converter.jsonToConvertibleMap(content, Converter.Type.CHANNEL).values().stream()
                    .map(JsonChannelLink.class::cast)
                    .collect(Collectors.toList());
            log.trace("channelsList={} with size={}", channelsList, channelsList.size());
            System.out.printf("Found %s channel links.%n", channelsList.size());

            // search items files:
            List<String> itemsFiles = findItemsFilesInDir(directory);
            List<String> itemnamesList = itemsFiles.stream()
                    .map(ChannelAppender::getItemnamesFromFile)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            log.trace("itemnamesList={} with size={}", itemnamesList, itemnamesList.size());
            System.out.printf("Found %s items.%n", itemnamesList.size());

            // only items present in both lists:
            List<String> newItemnamesList = itemnamesList.stream().filter(n -> {
                for (JsonChannelLink channel : channelsList) {
                    if (channel.getValue().getItemName().equals(n)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            List<JsonChannelLink> relevantChannelsList = channelsList.stream()
                    .filter(c -> newItemnamesList.contains(c.getValue().getItemName())).collect(Collectors.toList());
            log.trace("relevantChannelsList={} with size={}",
                    relevantChannelsList, relevantChannelsList.size());
            System.out.printf("%s match with each other.%n", relevantChannelsList.size());

            int count = 0;
            for (JsonChannelLink channel : relevantChannelsList) {
                for (String iFile : itemsFiles) {
                    if (setChannelToItemInFile(String.join(":", channel.getValue().getChannelUID().getSegments()), channel.getValue().getItemName(), iFile))
                        count++;
                }
            }
            log.trace("Added count={} times", count);
            System.out.printf("Successfully appended %s channel links!%n", count);

            System.out.println("Warning: You might need to manually fix some converting mistakes (double channels, etc.)");
        } catch (FileNotFoundException e) {
            log.error("File {} not found", channelLinkFile);
        }
    }

    /**
     * Returns a {@link List} of Strings containing the names of all items in
     * {@code fileName}.
     *
     * @param fileName the file(-Name) to open and search for items
     * @return a {@link List} containing the names of the items
     */
    public static List<String> getItemnamesFromFile(String fileName) {
        File file = new File(fileName);

        List<String> returnVal = new ArrayList<>();
        Scanner sc;
        try {
            sc = new Scanner(file);
            log.info("Reading lines of file={} with Scanner={}", file, sc);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line != null && !line.equalsIgnoreCase("")
                        && !line.toLowerCase().startsWith("Group".toLowerCase())) {
                    String itemName = searchNameInLine(line);
                    if (itemName != null && !itemName.equalsIgnoreCase("")) {
                        returnVal.add(itemName);
                    }
                }
            }

            sc.close();
        } catch (FileNotFoundException eF) {
            log.error(FATAL, "file={} can not be opened!", file);
            eF.printStackTrace();
        }

        return returnVal;
    }

    /**
     * Appends the {@code channel} after {@code itemName} in {@code fileName}.
     *
     * @param channel  the channel to append after the item
     * @param itemName the item to search for
     * @param fileName the file to search for the item
     * @return {@code true} if the {@code channel} could be appended, {@code false}
     * otherwise
     */
    public static boolean setChannelToItemInFile(String channel, String itemName, String fileName) {
        File file = new File(fileName);

        boolean returnVal = false;

        List<String> saveLines = new ArrayList<>();
        boolean change = false;

        Scanner sc;
        try {
            sc = new Scanner(file);

            log.info("Reading lines of file={} with Scanner={}", file, sc);
            while (sc.hasNextLine()) {

                String line = sc.nextLine();
                if (line != null && !line.equalsIgnoreCase("")
                        && !line.toLowerCase().startsWith("Group".toLowerCase())) {

                    String readItemName = searchNameInLine(line);
                    if (readItemName != null && !readItemName.equalsIgnoreCase("") && readItemName.equals(itemName)) {

                        line = String.format("%-160s {channel=\"%s\"}", line, channel).trim();
                        change = true;
                    }
                }

                saveLines.add(line);
            }
            sc.close();

            if (change) {
                returnVal = Converter.writeLinesToFile(saveLines, fileName);
            }
        } catch (FileNotFoundException eF) {
            log.error(FATAL, "file={} can not be opened!", file);
            eF.printStackTrace();
        }

        return returnVal;
    }

    /**
     * Searches for an item name in the {@code line}.
     *
     * @param line the line to search in
     * @return name of the item if found, {@code null} otherwise
     */
    public static String searchNameInLine(String line) {
        String[] arr = line.trim().split("\\s+");

        String returnVal = null;
        if (arr.length >= 2) {
            returnVal = arr[1];
        }

        return returnVal;
    }

    /**
     * Searches for ".items" files in the {@code directory}.
     *
     * @param directory the directory to search in
     * @return a {@link List} with all file paths of ".items" files
     */
    public static List<String> findItemsFilesInDir(String directory) {
        return Arrays.stream(new File(directory).listFiles((dir, filename) -> filename.endsWith(".items")))
                .map(File::getAbsolutePath)
                .collect(Collectors.toList());
    }
}
