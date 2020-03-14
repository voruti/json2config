package voruti.json2config;

import java.util.logging.Logger;

/**
 * @author voruti
 */
public class Channel implements IConvertible {

	private static final String CLASS_NAME = Channel.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	private String itemName;
	private String channelUID;

	@Override
	public String toConfigLine(String name) {
		String output = "";
		LOGGER.exiting(CLASS_NAME, "toConfigLine", output);
		return output;
	}

	/**
	 * @return the itemName
	 */
	public String getItemName() {
		LOGGER.exiting(CLASS_NAME, "getItemName", itemName);
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		LOGGER.entering(CLASS_NAME, "setItemName", itemName);
		this.itemName = itemName;
	}

	/**
	 * @return the channelUID
	 */
	public String getChannelUID() {
		LOGGER.exiting(CLASS_NAME, "getChannelUID", channelUID);
		return channelUID;
	}

	/**
	 * @param channelUID the channelUID to set
	 */
	public void setChannelUID(String channelUID) {
		LOGGER.entering(CLASS_NAME, "setChannelUID", channelUID);
		this.channelUID = channelUID;
	}

	@Override
	public String toString() {
		return String.format("Channel [itemName=%s, channelUID=%s]", itemName, channelUID);
	}

}
