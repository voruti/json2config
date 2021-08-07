package voruti.json2config;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import voruti.json2config.service.ChannelAppender;
import voruti.json2config.service.Converter;
import voruti.json2config.service.Type;

/**
 * @author voruti
 */
@Command(name = "java -jar json2config.jar", mixinStandardHelpOptions = true, version = "1.5",
        description = "Converts openHAB Items from JsonDB Storage files.")
public class Starter implements Runnable {

    @Option(names = {"--no-items", "-n"},
            description = "disable the converting feature completely; if you want to only append the channel links")
    private boolean noConverter;

    @Option(names = {"-c", "--channel", "--channel-link", "--create-channels", "--create-channel-links"},
            description = "enable the appending feature")
    private boolean doChannelLinks;


    @Option(names = {"-i", "--in", "--json"},
            defaultValue = "org.eclipse.smarthome.core.items.Item.json",
            description = "specify the input .json file")
    private String jsonFile;

    @Option(names = {"--channel-file", "--channel-link-file"},
            defaultValue = "org.eclipse.smarthome.core.thing.link.ItemChannelLink.json",
            description = "specify the .json file location containing the channel links")
    private String channelFile;

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
        // start Converter:
        if (!noConverter) {
            Converter.start(jsonFile, outFile, Type.ITEM);
        }

        // start ChannelAppender:
        if (doChannelLinks) {
            ChannelAppender.start(channelFile, directory);
        }
    }
}
