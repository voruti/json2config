package voruti.json2config.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.model.IAppendable;
import voruti.json2config.model.json.JsonMetadata;

/**
 * @author sbholmes
 */
@Slf4j
public class MetadataAppender {
	
	private MetadataAppender() {
	}
	
	/**
     * Appends the metadata from {@code metadataFile} to all ".items" files
     * in the {@code directory}.
     *
     * @param metadataFile path to the file which contains the metadata in
     *                        JSON format
     * @param directory       the directory in which to search for ".items" files
     */
    public static void start(String metadataFile, String directory) {
        log.debug("Starting MetadataAppender with metadataFile={}, directory={}", metadataFile, directory);

        try {
            // open file:
            String content = SharedService.openFileToString(metadataFile);
            // map to list of metadata:
            List<IAppendable> metadataList = SharedService.jsonToConvertibleMap(content, Type.METADATA).values().stream()
                    .map(JsonMetadata.class::cast)
                    .collect(Collectors.toList());
            log.info("Found {} metadata", metadataList.size());
            log.trace("metadataList={}", metadataList);

            Appender.searchAndAppend(directory, metadataList);

        } catch (IOException e) {
            log.error(Constants.LOG_CANT_OPEN_FILE, metadataFile);
        }
    }

}
