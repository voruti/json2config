package voruti.json2config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import voruti.json2config.Converter.Type;

/**
 * @author voruti
 */
public class Starter {

	private static final String CLASS_NAME = Starter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	private static final Level LEVEL = Level.WARNING;

	private static final String DEFAULT_JSONFILE = "org.eclipse.smarthome.core.items.Item.json";
	private static final String DEFAULT_CHANNELFILE = "org.eclipse.smarthome.core.thing.link.ItemChannelLink.json";
	private static final String DEFAULT_OUTFILE = "json.items";
	private static final String DEFAULT_DIRECTORY = ".";

	public static void main(String[] args) {
		// logging:
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF_%1$tT][%2$-50.50s][%4$13.13s]: %5$s%n");
		LOGGER.getParent().setLevel(LEVEL);
		LOGGER.getParent().getHandlers()[0].setLevel(LEVEL);

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

		loop: for (int i = 0; i < args.length; i++) {
			if (inNext) {
				jsonFile = args[i];
				LOGGER.log(Level.INFO, "Using jsonFile={0}", jsonFile);
				inNext = false;
			} else if (chNext) {
				channelFile = args[i];
				LOGGER.log(Level.INFO, "Using channelFile={0}", channelFile);
				chNext = false;
			} else if (outNext) {
				outFile = args[i];
				LOGGER.log(Level.INFO, "Using outFile={0}", outFile);
				outNext = false;
			} else if (dirNext) {
				directory = args[i];
				LOGGER.log(Level.INFO, "Using directory={0}", directory);
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

				case "-l":
				case "--log":
				case "--enable-logging":
					LOGGER.getParent().setLevel(Level.ALL);
					LOGGER.getParent().getHandlers()[0].setLevel(Level.INFO);

					// logging to file:
					FileHandler fileHandler;
					try {
						fileHandler = new FileHandler("latest.log");
					} catch (SecurityException | IOException e) {
						e.printStackTrace();
						return;
					}
					fileHandler.setLevel(Level.ALL);
					fileHandler.setFormatter(new SimpleFormatter());
					LOGGER.getParent().addHandler(fileHandler);
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
			LOGGER.log(Level.WARNING, "Wrong parameter usage");
			System.out.println(
					"Usage: json2config-XXX.jar [--log] [--in <path>] [--out <path>] [--no-items] [--create-channel-links] [--directory] [--channel-file]");
			return;
		}

		// start Converter:
		if (doConverter) {
			Type type = Type.ITEM;
			LOGGER.log(Level.INFO, "Starting Converter with jsonFile={0}, outFile={1}, type={2}",
					new Object[] { jsonFile, outFile, type });
			new Converter(jsonFile, outFile, type);
		}

		// start ChannelAppender:
		if (doChannelLinks) {
			LOGGER.log(Level.INFO, "Starting ChannelAppender with channelFile={0}, directory={1}",
					new Object[] { channelFile, directory });
			new ChannelAppender(channelFile, directory);
		}
	}

}
