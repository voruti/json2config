package voruti.json2config;

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
	public String toConfigLine(String name);

}
