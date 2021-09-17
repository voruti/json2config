package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.model.json.JsonChannelLink;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author voruti
 */
@Slf4j
public class ChannelAppender {

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
        log.debug("Starting ChannelAppender with channelLinkFile={}, directory={}", channelLinkFile, directory);

        try {
            // open file:
            String content = SharedService.openFileToString(channelLinkFile);
            // map to list of channel links:
            List<JsonChannelLink> channelLinkList = SharedService.jsonToConvertibleMap(content, Type.CHANNEL).values().stream()
                    .map(JsonChannelLink.class::cast)
                    .collect(Collectors.toList());
            log.info("Found {} channel links", channelLinkList.size());
            log.trace("channelLinkList={}", channelLinkList);

            // search items files:
            List<String> itemsFiles = Appender.findItemsFilesInDir(directory);
            // get names of all items:
            List<String> itemNamesList = itemsFiles.stream()
                    .map(Appender::getItemNamesFromFile)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
            log.info("Found {} items", itemNamesList.size());
            log.trace("itemNamesList={}", itemNamesList);

            // only channels present in both lists:
            List<JsonChannelLink> relevantChannelLinkList = channelLinkList.stream()
                    .filter(channelLink -> itemNamesList.stream()
                            .anyMatch(itemName -> itemName.equals(channelLink.getValue().getItemName())))
                    .collect(Collectors.toList());
            log.info("{} match with each other", relevantChannelLinkList.size());
            log.trace("relevantChannelLinkList={}", relevantChannelLinkList);

            int count = 0;
            for (JsonChannelLink channel : relevantChannelLinkList) {
                for (String iFile : itemsFiles) {
                    if (Appender.appendToItemInFile(channel, iFile)) {
                        count++;
                    }
                }
            }
            log.info("Successfully appended {} channel links!", count);

            log.warn("Warning: You might need to manually fix some converting mistakes (double channels, etc.)");
        } catch (IOException e) {
            log.error(Constants.LOG_CANT_OPEN_FILE, channelLinkFile);
        }
    }

}
