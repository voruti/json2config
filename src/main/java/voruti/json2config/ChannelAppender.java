package voruti.json2config;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author voruti
 */
public class ChannelAppender {

    private static final String CLASS_NAME = ChannelAppender.class.getName();
    private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

    /**
     * Appends the channel links from {@code channelLinkFile} to all ".items" files
     * in the {@code directory}.
     *
     * @param channelLinkFile path to the file which contains the channel links in
     *                        JSON format
     * @param directory       the directory in which to search for ".items" files
     */
    public ChannelAppender(String channelLinkFile, String directory) {
        LOGGER.entering(CLASS_NAME, "<init>", new Object[]{channelLinkFile, directory});

        // get the jsonObject:
        JSONObject jsonObject = Converter.openFileToJSONObject(channelLinkFile);
        // convert first elements to list of Channels:
        List<Channel> channelsList = Converter.goThroughFirstEntrysOfJSONObject(jsonObject, Converter.Type.CHANNEL)
                .values().stream().map(c -> (Channel) c).collect(Collectors.toList());
        LOGGER.log(Level.FINE, "channelsList={0} with size={1}", new Object[]{channelsList, channelsList.size()});
        System.out.println(String.format("Found %s channel links.", channelsList.size()));

        // search items files:
        List<String> itemsFiles = findItemsFilesInDir(directory);
        List<String> itemnamesList = itemsFiles.stream().map(f -> getItemnamesFromFile(f)).flatMap(Collection::stream)
                .collect(Collectors.toList());
        LOGGER.log(Level.FINE, "itemnamesList={0} with size={1}", new Object[]{itemnamesList, itemnamesList.size()});
        System.out.println(String.format("Found %s items.", itemnamesList.size()));

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
        LOGGER.log(Level.FINE, "relevantChannelsList={0} with size={1}",
                new Object[]{relevantChannelsList, relevantChannelsList.size()});
        System.out.println(String.format("%s match with each other.", relevantChannelsList.size()));

        int count = 0;
        for (Channel channel : relevantChannelsList) {
            for (String iFile : itemsFiles) {
                if (setChannelToItemInFile(channel.getChannelUID(), channel.getItemName(), iFile))
                    count++;
            }
        }
        LOGGER.log(Level.FINE, "Added count={0} times", count);
        System.out.println(String.format("Successfully appended %s channel links!", count));

        System.out.println("Warning: You might need to manually fix some converting mistakes (double channels, etc.)");

        LOGGER.exiting(CLASS_NAME, "<init>");
    }

    /**
     * Creates a {@link Channel} out of a {@link JSONObject}.
     *
     * @param content the {@link JSONObject}
     * @return the channel as {@link Channel}
     */
    public static Channel createChannel(JSONObject content) {
        LOGGER.entering(CLASS_NAME, "createChannel", content);

        String itemName = "";
        String channelUID = "";

        Iterator<String> ite1 = content.keys();
        while (ite1.hasNext()) {
            String key1 = ite1.next();
            Object val1 = content.get(key1);

            switch (key1) {
                case "class":
                    if (!val1.equals("org.eclipse.smarthome.core.thing.link.ItemChannelLink")) {
                        LOGGER.log(Level.WARNING, "class={0} different than expected!", val1);
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

                                            switch (key3) {
                                                case "segments":
                                                    if (val3 instanceof JSONArray) {
                                                        for (Object o : (JSONArray) val3) {
                                                            if (o instanceof String) {
                                                                if (!channelUID.equalsIgnoreCase("")) {
                                                                    channelUID += ":";
                                                                }
                                                                channelUID += (String) o;
                                                            } else {
                                                                LOGGER.log(Level.WARNING,
                                                                        "JSONArray={0} item={1} is not instanceof String!",
                                                                        new Object[]{key3, o});
                                                            }
                                                        }
                                                    } else {
                                                        LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONArray!",
                                                                new Object[]{key3, val3});
                                                    }
                                                    break;

                                                default:
                                                    LOGGER.log(Level.WARNING, "Unexpected key={0}", key3);
                                                    break;
                                            }
                                        }
                                    } else {
                                        LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONObject!",
                                                new Object[]{key2, val2});
                                    }
                                case "configuration":
                                    break;
                                case "itemName":
                                    if (val2 instanceof String) {
                                        itemName = (String) val2;
                                    } else {
                                        LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
                                                new Object[]{key2, val2});
                                    }
                                    break;

                                default:
                                    LOGGER.log(Level.WARNING, "Unexpected key={0}", key2);
                                    break;
                            }
                        }
                        break;
                    } else {
                        LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONObject!", new Object[]{key1, val1});
                    }

                default:
                    LOGGER.log(Level.WARNING, "Unexpected key={0}", key1);
                    break;
            }
        }

        Channel returnVal = new Channel();
        returnVal.setItemName(itemName);
        returnVal.setChannelUID(channelUID);

        LOGGER.exiting(CLASS_NAME, "createChannel", returnVal);
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
        LOGGER.entering(CLASS_NAME, "getItemnamesFromFile", fileName);

        File file = new File(fileName);

        List<String> returnVal = new ArrayList<>();
        Scanner sc;
        try {
            sc = new Scanner(file);
            LOGGER.log(Level.INFO, "Reading lines of file={0} with Scanner={1}", new Object[]{file, sc});

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
            LOGGER.log(Level.SEVERE, "file={0} can not be opened!", file);
            eF.printStackTrace();
        }

        LOGGER.exiting(CLASS_NAME, "getItemnamesFromFile", returnVal);
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
        LOGGER.entering(CLASS_NAME, "setChannelToItemInFile", new Object[]{channel, itemName, fileName});

        File file = new File(fileName);

        boolean returnVal = false;

        List<String> saveLines = new ArrayList<>();
        boolean change = false;

        Scanner sc;
        try {
            sc = new Scanner(file);

            LOGGER.log(Level.INFO, "Reading lines of file={0} with Scanner={1}", new Object[]{file, sc});
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
            LOGGER.log(Level.SEVERE, "file={0} can not be opened!", file);
            eF.printStackTrace();
        }

        LOGGER.exiting(CLASS_NAME, "setChannelToItemInFile", returnVal);
        return returnVal;
    }

    /**
     * Searches for a item name in the {@code line}.
     *
     * @param line the line to search in
     * @return name of the item if found, {@code null} otherwise
     */
    public static String searchNameInLine(String line) {
        LOGGER.entering(CLASS_NAME, "searchNameInLine", line);

        String[] arr = line.trim().split("\\s+");

        String returnVal = null;
        if (arr.length >= 2) {
            returnVal = arr[1];
        }

        LOGGER.exiting(CLASS_NAME, "searchNameInLine", returnVal);
        return returnVal;
    }

    /**
     * Searches for ".items" files in the {@code directory}.
     *
     * @param directory the directory to search in
     * @return a {@link List} with all file paths of ".items" files
     */
    public static List<String> findItemsFilesInDir(String directory) {
        LOGGER.entering(CLASS_NAME, "findItemsFilesInDir", directory);

        List<String> returnVal = Arrays
                .stream(new File(directory).listFiles((dir, filename) -> filename.endsWith(".items")))
                .map(f -> f.getAbsolutePath()).collect(Collectors.toList());

        LOGGER.exiting(CLASS_NAME, "findItemsFilesInDir", returnVal);
        return returnVal;
    }
}
