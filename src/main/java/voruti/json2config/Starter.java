package voruti.json2config;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * @author voruti
 */
public class Starter {

	private static final String CLASS_NAME = Starter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);
	private static final Level LEVEL = Level.WARNING;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF_%1$tT][%2$-40.40s][%4$13.13s]: %5$s%n");
		LOGGER.getParent().setLevel(LEVEL);
		LOGGER.getParent().getHandlers()[0].setLevel(Level.SEVERE);
		FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("latest.log");
		} catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		fileHandler.setLevel(LEVEL);
		fileHandler.setFormatter(new SimpleFormatter());
		LOGGER.getParent().addHandler(fileHandler);

		new Json("org.eclipse.smarthome.core.items.Item.json");
	}

}
