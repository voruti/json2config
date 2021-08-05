package voruti.json2config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import voruti.json2config.service.ChannelAppender;
import voruti.json2config.service.Converter;
import voruti.json2config.service.Converter.Type;

/**
 * @author voruti
 */
public class Starter {

    private static final Logger LOGGER = LoggerFactory.getLogger(Starter.class);

    private static final String DEFAULT_JSONFILE = "org.eclipse.smarthome.core.items.Item.json";
    private static final String DEFAULT_CHANNELFILE = "org.eclipse.smarthome.core.thing.link.ItemChannelLink.json";
    private static final String DEFAULT_OUTFILE = "json.items";
    private static final String DEFAULT_DIRECTORY = ".";

    public static void main(String[] args) {
        // args evaluating:
        boolean inNext = false;
        boolean chNext = false;
        boolean outNext = false;
        boolean dirNext = false;

        boolean printHelp = false;
        boolean doConverter = true; // default true
        boolean doChannelLinks = false;

        String jsonFile = DEFAULT_JSONFILE;
        String channelFile = DEFAULT_CHANNELFILE;
        String outFile = DEFAULT_OUTFILE;
        String directory = DEFAULT_DIRECTORY;

        loop:
        for (int i = 0; i < args.length; i++) {
            if (inNext) {
                jsonFile = args[i];
                LOGGER.info("Using jsonFile={}", jsonFile);
                inNext = false;
            } else if (chNext) {
                channelFile = args[i];
                LOGGER.info("Using channelFile={}", channelFile);
                chNext = false;
            } else if (outNext) {
                outFile = args[i];
                LOGGER.info("Using outFile={}", outFile);
                outNext = false;
            } else if (dirNext) {
                directory = args[i];
                LOGGER.info("Using directory={}", directory);
                dirNext = false;
            } else {
                switch (args[i]) {
                    case "-i":
                    case "--in":
                    case "--json":
                        if (args.length >= i + 2) {
                            inNext = true;
                        }
                        break;

                    case "--channel-file":
                    case "--channel-link-file":
                        if (args.length >= i + 2) {
                            chNext = true;
                        }
                        break;

                    case "-o":
                    case "--out":
                    case "--items":
                        if (args.length >= i + 2) {
                            outNext = true;
                        }
                        break;

                    case "-d":
                    case "--dir":
                    case "--directory":
                        if (args.length >= i + 2) {
                            dirNext = true;
                        }
                        break;

                    case "-c":
                    case "--channel":
                    case "--channel-link":
                    case "--create-channels":
                    case "--create-channel-links":
                        doChannelLinks = true;
                        break;

                    case "--no-items":
                    case "-n":
                        doConverter = false;
                        break;

                    default:
                        printHelp = true;
                        break loop;
                }
            }
        }
        if (printHelp) {
            LOGGER.warn("Wrong parameter usage");
            System.out.println(
                    "Usage: json2config-XXX.jar [--in <path>] [--out <path>] [--no-items] [--create-channel-links] [--directory] [--channel-file]");
            return;
        }

        // start Converter:
        if (doConverter) {
            Type type = Type.ITEM;
            LOGGER.info("Starting Converter with jsonFile={}, outFile={}, type={}",
                    jsonFile, outFile, type);
            new Converter(jsonFile, outFile, type);
        }

        // start ChannelAppender:
        if (doChannelLinks) {
            LOGGER.info("Starting ChannelAppender with channelFile={}, directory={}",
                    channelFile, directory);
            new ChannelAppender(channelFile, directory);
        }
    }
}
