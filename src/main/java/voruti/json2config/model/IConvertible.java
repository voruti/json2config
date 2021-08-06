package voruti.json2config.model;

/**
 * @author voruti
 */
public interface IConvertible {

    /**
     * Converts an object/convertible to a config file line.
     *
     * @return a String representating a config file line
     */
    String toConfigLine();
}
