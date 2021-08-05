package voruti.json2config.model;

import org.eclipse.smarthome.core.items.ManagedItemProvider.PersistedItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author voruti
 */
public class Item extends PersistedItem implements IConvertible {

    private static final Logger LOGGER = LoggerFactory.getLogger(Item.class);

    public Item(String itemType) {
        super(itemType);
        LOGGER.trace("{} constructed", this);
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
        String baseItemTypeString = "";
        if (!baseItemType.equalsIgnoreCase("")) {
            baseItemTypeString = ":" + baseItemType;
        }

        String functionNameString = "";
        if (!functionName.equalsIgnoreCase("")) {
            functionNameString = ":" + functionName;
        }

        StringBuilder functionParamsString = new StringBuilder();
        if (!functionParams.isEmpty()) {
            functionParamsString.append("(");
        }
        for (String string : functionParams) {
            if (!functionParamsString.toString().equalsIgnoreCase("(")) {
                functionParamsString.append(",");
            }
            functionParamsString.append(string);
        }
        if (!functionParams.isEmpty()) {
            functionParamsString.append(")");
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

        StringBuilder groupNamesString = new StringBuilder();
        if (!groupNames.isEmpty()) {
            groupNamesString.append("(");
        }
        for (String string : groupNames) {
            if (!groupNamesString.toString().equalsIgnoreCase("(")) {
                groupNamesString.append(", ");
            }
            groupNamesString.append(string);
        }
        if (!groupNames.isEmpty()) {
            groupNamesString.append(")");
        }

        StringBuilder tagsString = new StringBuilder();
        if (!tags.isEmpty()) {
            tagsString.append("[");
        }
        for (String string : tags) {
            if (!tagsString.toString().equalsIgnoreCase("[")) {
                tagsString.append(", ");
            }
            tagsString.append(string);
        }
        if (!tags.isEmpty()) {
            tagsString.append("]");
        }

        return String.format("%-30s %-40s %-20s %-20s %-20s %-20s", beginString, name, labelString,
                categoryString, groupNamesString, tagsString).strip();
    }
}
