package voruti.json2config.model;

/**
 * @author sbholmes
 */
public interface IAppendable extends IConvertible {

    /**
     * Provides a way to get the item name from the channel or metadata so it can be appended to the right item
     * 
     * @return the name of the item
     */
    String getItemName();

}
