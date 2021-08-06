package voruti.json2config;

import lombok.extern.slf4j.Slf4j;
import voruti.json2config.service.ChannelAppender;
import voruti.json2config.service.Converter;
import voruti.json2config.service.Converter.Type;

/**
 * @author voruti
 */
@Slf4j
public class Starter {

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
                log.info("Using jsonFile={}", jsonFile);
                inNext = false;
            } else if (chNext) {
                channelFile = args[i];
                log.info("Using channelFile={}", channelFile);
                chNext = false;
            } else if (outNext) {
                outFile = args[i];
                log.info("Using outFile={}", outFile);
                outNext = false;
            } else if (dirNext) {
                directory = args[i];
                log.info("Using directory={}", directory);
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
            log.warn("Wrong parameter usage");
            System.out.println(
                    "Usage: json2config-XXX.jar [--in <path>] [--out <path>] [--no-items] [--create-channel-links] [--directory] [--channel-file]");
            return;
        }

        // start Converter:
        if (doConverter) {
            Type type = Type.ITEM;
            log.info("Starting Converter with jsonFile={}, outFile={}, type={}",
                    jsonFile, outFile, type);
            Converter.start(jsonFile, outFile, type);
        }

        // start ChannelAppender:
        if (doChannelLinks) {
            log.info("Starting ChannelAppender with channelFile={}, directory={}",
                    channelFile, directory);
            ChannelAppender.start(channelFile, directory);
        }
    }
}
