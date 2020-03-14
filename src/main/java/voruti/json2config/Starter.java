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
	private static final String DEFAULT_OUTFILE = "json.items";

	public static void main(String[] args) {
		// logging:
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF_%1$tT][%2$-40.40s][%4$13.13s]: %5$s%n");
		LOGGER.getParent().setLevel(LEVEL);
		LOGGER.getParent().getHandlers()[0].setLevel(LEVEL);

		// args evaluating:
		boolean inNext = false;
		boolean outNext = false;
		boolean printHelp = false;

		String jsonFile = DEFAULT_JSONFILE;
		String outFile = DEFAULT_OUTFILE;
		loop: for (int i = 0; i < args.length; i++) {
			if (inNext) {
				jsonFile = args[i];
				LOGGER.log(Level.INFO, "Using jsonFile={0}", jsonFile);
				inNext = false;
			} else if (outNext) {
				outFile = args[i];
				LOGGER.log(Level.INFO, "Using outFile={0}", outFile);
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

				default:
					printHelp = true;
					break loop;
				}
			}
		}
		if (printHelp) {
			LOGGER.log(Level.WARNING, "Wrong parameter usage");
			System.out.println("Usage: JSON2Config.jar [--in <path>] [--out <path>] [--log]");
			return;
		}

		// start:
		Type type = Type.ITEM;
		LOGGER.log(Level.INFO, "Starting program with jsonFile={0}, outFile={1}, type={2}",
				new Object[] { jsonFile, outFile, type });
		new Converter(jsonFile, outFile, type);
	}

}
