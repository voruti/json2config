package voruti.json2config;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import voruti.json2config.service.ChannelAppender;
import voruti.json2config.service.Constants;
import voruti.json2config.service.Converter;
import voruti.json2config.service.MetadataAppender;
import voruti.json2config.service.Type;

/**
 * @author voruti
 */
@Command(name = "java -jar json2config.jar", mixinStandardHelpOptions = true, version = "1.8.0", // change me on new release
        description = "Converts openHAB Items from JsonDB Storage files.")
public class Starter implements Runnable {

    @Option(names = {"--no-items", "-n"},
            description = "disable the converting feature completely; if you want to only append the channel links")
    private boolean noConverter;

    @Option(names = {"-c", "--channel", "--channel-link", "--create-channels", "--create-channel-links"},
            description = "enable the appending feature")
    private boolean doChannelLinks;

    @Option(names = {"-m", "--metadata", "--append-metadata"},
            description = "enable the metadata appending feature")
    private boolean doMetadata;

    @Option(names = {"-3", "--openhab3", "--v3", "--openhab-v3", "--openhabv3", "--openhab-3"},
            description = "set default file names used since openHAB version 3.X")
    private boolean defaultV3;


    @Option(names = {"-i", "--in", "--json"},
            defaultValue = Constants.DEFAULT_V2_JSON_FILE,
            description = "specify the input .json file")
    private String jsonFile;

    @Option(names = {"--channel-file", "--channel-link-file"},
            defaultValue = Constants.DEFAULT_V2_CHANNEL_FILE,
            description = "specify the .json file location containing the channel links")
    private String channelFile;

    @Option(names = {"--metadata-file"},
            defaultValue = Constants.DEFAULT_V2_METADATA_FILE,
            description = "specify the .json file location containing the metadata")
    private String metadataFile;

    @Option(names = {"-o", "--out", "--items"},
            defaultValue = "json.items",
            description = "specify the output file")
    private String outFile;

    @Option(names = {"-d", "--dir", "--directory"},
            defaultValue = ".",
            description = "specify the directory in which to search for *.items files")
    private String directory;


    public static void main(String[] args) {
        new CommandLine(new Starter()).execute(args);
    }


    /**
     * Starts execution after args are evaluated.
     */
    @Override
    public void run() {
        // openHAB 3.X defaults:
        if (defaultV3) {
            if (jsonFile.equals(Constants.DEFAULT_V2_JSON_FILE)) {
                jsonFile = Constants.DEFAULT_V3_JSON_FILE;
            }
            if (channelFile.equals(Constants.DEFAULT_V2_CHANNEL_FILE)) {
                channelFile = Constants.DEFAULT_V3_CHANNEL_FILE;
            }
            if (metadataFile.equals(Constants.DEFAULT_V2_METADATA_FILE)) {
                metadataFile = Constants.DEFAULT_V3_METADATA_FILE;
            }
        }

        // start Converter:
        if (!noConverter) {
            Converter.start(jsonFile, outFile, Type.ITEM);
        }

        // start ChannelAppender:
        if (doChannelLinks) {
            ChannelAppender.start(channelFile, directory);
        }

        // start MetadataAppender:
        if (doMetadata) {
            MetadataAppender.start(metadataFile, directory);
        }
    }
}
