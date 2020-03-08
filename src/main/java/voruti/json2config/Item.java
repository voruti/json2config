package voruti.json2config;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.smarthome.core.items.ManagedItemProvider.PersistedItem;

/**
 * @author voruti
 *
 */
public class Item extends PersistedItem implements IConvertible {

	private static final String CLASS_NAME = Converter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * @param itemType
	 */
	public Item(String itemType) {
		super(itemType);
		LOGGER.log(Level.FINE, "{0} constructed", this);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format(
				"MyItem [baseItemType=%-8.8s, groupNames=%-20.20s, itemType=%-10.10s, tags=%-5.5s, label=%-15.15s, category=%-15.15s, functionName=%-10.10s, functionParams=%-15.15s, dimension=%-5.5s]",
				baseItemType, groupNames != null ? toString(groupNames, maxLen) : null, itemType,
				tags != null ? toString(tags, maxLen) : null, label, category, functionName,
				functionParams != null ? toString(functionParams, maxLen) : null, dimension);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

	/**
	 * Formats a valid .items-file line. Uses every field except dimension (it's not
	 * used? / within itemType).
	 * 
	 * @param name the name of the item
	 * @return a item config line
	 */
	@Override
	public String toConfigLine(String name) {
		LOGGER.entering(CLASS_NAME, "toConfigLine", name);

		String baseItemTypeString = "";
		if (!baseItemType.equalsIgnoreCase("")) {
			baseItemTypeString = ":" + baseItemType;
		}

		String functionNameString = "";
		if (!functionName.equalsIgnoreCase("")) {
			functionNameString = ":" + functionName;
		}

		String functionParamsString = "";
		if (functionParams.size() != 0) {
			functionParamsString = "(";
		}
		for (String string : functionParams) {
			if (!functionParamsString.equalsIgnoreCase("(")) {
				functionParamsString += ",";
			}
			functionParamsString += string;
		}
		if (functionParams.size() != 0) {
			functionParamsString += ")";
		}

		String beginString;
		if (itemType.equalsIgnoreCase("Group")) {
			beginString = String.format("%s%s%s%s", itemType, baseItemTypeString, functionNameString,
					functionParamsString);
		} else {
			beginString = itemType;
		}

		String labelString;
		if (label.equalsIgnoreCase("")) {
			labelString = "";
		} else {
			labelString = String.format("\"%s\"", label);
		}

		String categoryString;
		if (category.equalsIgnoreCase("")) {
			categoryString = "";
		} else {
			categoryString = String.format("<%s>", category.toLowerCase());
		}

		String groupNamesString = "";
		if (groupNames.size() != 0) {
			groupNamesString = "(";
		}
		for (String string : groupNames) {
			if (!groupNamesString.equalsIgnoreCase("(")) {
				groupNamesString += ", ";
			}
			groupNamesString += string;
		}
		if (groupNames.size() != 0) {
			groupNamesString += ")";
		}

		String tagsString = "";
		if (tags.size() != 0) {
			tagsString = "[";
		}
		for (String string : tags) {
			if (!tagsString.equalsIgnoreCase("[")) {
				tagsString += ", ";
			}
			tagsString += string;
		}
		if (tags.size() != 0) {
			tagsString += "]";
		}

		String output = String.format("%-30s %-40s %-20s %-20s %-20s %-20s", beginString, name, labelString,
				categoryString, groupNamesString, tagsString).trim();
		LOGGER.exiting(CLASS_NAME, "toConfigLine", output);
		return output;
	}

}
