package voruti.json2config.service;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.model.IAppendable;
import voruti.json2config.model.json.JsonChannelLink;

import java.io.IOException;
import java.util.List;
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
     * @param channelLinkFile path to the file which contains the channel links in JSON format
     * @param directory       the directory in which to search for ".items" files
     */
    public static void start(String channelLinkFile, String directory) {
        log.debug("Starting ChannelAppender with channelLinkFile={}, directory={}", channelLinkFile, directory);

        try {
            // open file:
            String content = SharedService.openFileToString(channelLinkFile);
            // map to list of channel links:
            List<IAppendable> channelLinkList = SharedService.jsonToConvertibleMap(content, Type.CHANNEL).values().stream()
                    .map(JsonChannelLink.class::cast)
                    .collect(Collectors.toList());
            log.info("Found {} channel links", channelLinkList.size());
            log.trace("channelLinkList={}", channelLinkList);

            Appender.searchAndAppend(directory, channelLinkList);
        } catch (IOException e) {
            log.error(Constants.LOG_CANT_OPEN_FILE, channelLinkFile);
        }
    }
}
