package voruti.json2config.model;

/**
 * @author voruti
 */
public interface IConvertible {

    /**
     * Converts a object/convertible to a config file line.
     *
     * @param name the name of the object/convertible
     * @return a String representating a config file line
     */
    String toConfigLine(String name);
}
