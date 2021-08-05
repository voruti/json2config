package voruti.json2config.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import voruti.json2config.model.Channel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author voruti
 */
public class ChannelAppender {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelAppender.class);
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
        // get the jsonObject:
        JSONObject jsonObject = Converter.openFileToJSONObject(channelLinkFile);
        // convert first elements to list of Channels:
        List<Channel> channelsList = Converter.goThroughFirstEntriesOfJSONObject(jsonObject, Converter.Type.CHANNEL).values().stream()
                .map(Channel.class::cast)
                .collect(Collectors.toList());
        LOGGER.trace("channelsList={} with size={}", channelsList, channelsList.size());
        System.out.printf("Found %s channel links.%n", channelsList.size());

        // search items files:
        List<String> itemsFiles = findItemsFilesInDir(directory);
        List<String> itemnamesList = itemsFiles.stream()
                .map(ChannelAppender::getItemnamesFromFile)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        LOGGER.trace("itemnamesList={} with size={}", itemnamesList, itemnamesList.size());
        System.out.printf("Found %s items.%n", itemnamesList.size());

        // only items present in both lists:
        List<String> newItemnamesList = itemnamesList.stream().filter(n -> {
            for (Channel channel : channelsList) {
                if (channel.getItemName().equals(n)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        List<Channel> relevantChannelsList = channelsList.stream()
                .filter(c -> newItemnamesList.contains(c.getItemName())).collect(Collectors.toList());
        LOGGER.trace("relevantChannelsList={} with size={}",
                relevantChannelsList, relevantChannelsList.size());
        System.out.printf("%s match with each other.%n", relevantChannelsList.size());

        int count = 0;
        for (Channel channel : relevantChannelsList) {
            for (String iFile : itemsFiles) {
                if (setChannelToItemInFile(channel.getChannelUID(), channel.getItemName(), iFile))
                    count++;
            }
        }
        LOGGER.trace("Added count={} times", count);
        System.out.printf("Successfully appended %s channel links!%n", count);

        System.out.println("Warning: You might need to manually fix some converting mistakes (double channels, etc.)");
    }

    /**
     * Creates a {@link Channel} out of a {@link JSONObject}.
     *
     * @param content the {@link JSONObject}
     * @return the channel as {@link Channel}
     */
    public static Channel createChannel(JSONObject content) {
        String itemName = "";
        StringJoiner channelUID = new StringJoiner(":");

        Iterator<String> ite1 = content.keys();
        while (ite1.hasNext()) {
            String key1 = ite1.next();
            Object val1 = content.get(key1);

            switch (key1) {
                case "class":
                    if (!val1.equals("org.eclipse.smarthome.core.thing.link.ItemChannelLink")) {
                        LOGGER.warn("class={} different than expected!", val1);
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
                                case "channelUID":
                                    if (val2 instanceof JSONObject) {
                                        JSONObject jso3 = (JSONObject) val2;
                                        Iterator<String> ite3 = jso3.keys();
                                        while (ite3.hasNext()) {
                                            String key3 = ite3.next();
                                            Object val3 = jso3.get(key3);

                                            if ("segments".equals(key3)) {
                                                Converter.mapArray(channelUID, key3, val3);
                                            } else {
                                                LOGGER.warn("Unexpected key={}", key3);
                                            }
                                        }
                                    } else {
                                        LOGGER.warn("{}={} is not instanceof JSONObject!",
                                                key2, val2);
                                    }
                                    break;
                                case "configuration":
                                    break;
                                case "itemName":
                                    if (val2 instanceof String) {
                                        itemName = (String) val2;
                                    } else {
                                        LOGGER.warn("{}={} is not instanceof String!",
                                                key2, val2);
                                    }
                                    break;

                                default:
                                    LOGGER.warn("Unexpected key={}", key2);
                                    break;
                            }
                        }
                        break;
                    } else {
                        LOGGER.warn("{}={} is not instanceof JSONObject!", key1, val1);
                    }
                    break;

                default:
                    LOGGER.warn("Unexpected key={}", key1);
                    break;
            }
        }

        Channel returnVal = new Channel();
        returnVal.setItemName(itemName);
        returnVal.setChannelUID(channelUID.toString());

        return returnVal;
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
            LOGGER.info("Reading lines of file={} with Scanner={}", file, sc);

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
            LOGGER.error(FATAL, "file={} can not be opened!", file);
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

            LOGGER.info("Reading lines of file={} with Scanner={}", file, sc);
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
            LOGGER.error(FATAL, "file={} can not be opened!", file);
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
