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
	private static final Level LEVEL = Level.ALL;

	private static final String DEFAULT_JSONFILE = "org.eclipse.smarthome.core.items.Item.json";
	private static final String DEFAULT_ITEMSFILE = "json.items";

	public static void main(String[] args) {
		// logging:
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF_%1$tT][%2$-40.40s][%4$13.13s]: %5$s%n");
		LOGGER.getParent().setLevel(LEVEL);
		LOGGER.getParent().getHandlers()[0].setLevel(Level.SEVERE);
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("latest.log");
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			return;
		}
		fileHandler.setLevel(LEVEL);
		fileHandler.setFormatter(new SimpleFormatter());
		LOGGER.getParent().addHandler(fileHandler);

		// args evaluating:
		boolean inNext = false;
		boolean outNext = false;
		boolean printHelp = false;

		String jsonFile = DEFAULT_JSONFILE;
		String itemsFile = DEFAULT_ITEMSFILE;
		loop: for (int i = 0; i < args.length; i++) {
			if (inNext) {
				jsonFile = args[i];
				LOGGER.log(Level.INFO, "Using jsonFile={0}", jsonFile);
				inNext = false;
			} else if (outNext) {
				itemsFile = args[i];
				LOGGER.log(Level.INFO, "Using itemsFile={0}", itemsFile);
				outNext = false;
			} else {
				switch (args[i]) {
				case "--in":
				case "-i":
				case "--json":
					if (args.length >= i + 2) {
						inNext = true;
					}
					break;

				case "--out":
				case "-o":
				case "--items":
					if (args.length >= i + 2) {
						outNext = true;
					}
					break;

				default:
					printHelp = true;
					break loop;
				}
			}
		}
		if (printHelp) {
			LOGGER.log(Level.WARNING, "Wrong parameter usage");
			System.out.println("Usage: JSON2Config.jar [--in <path>] [--out <path>]");
			return;
		}

		// start:
		LOGGER.log(Level.INFO, "Starting program with jsonFile={0}, itemsFile={1}",
				new Object[] { jsonFile, itemsFile });
		new Converter(jsonFile, itemsFile, Type.ITEM);
	}

}
