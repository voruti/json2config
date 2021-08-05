package voruti.json2config.model;

/**
 * @author voruti
 */
public class Channel implements IConvertible {

    private String itemName;
    private String channelUID;

    @Override
    public String toConfigLine(String name) {
        return "";
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the channelUID
     */
    public String getChannelUID() {
        return channelUID;
    }

    /**
     * @param channelUID the channelUID to set
     */
    public void setChannelUID(String channelUID) {
        this.channelUID = channelUID;
    }

    @Override
    public String toString() {
        return String.format("Channel [itemName=%s, channelUID=%s]", itemName, channelUID);
    }
}
