package voruti.json2config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * @author voruti
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Item implements IConvertible {

    private String baseItemType;
    private List<String> groupNames;
    private String itemType;
    private Set<String> tags;
    private String label;
    private String category;
    private String functionName;
    private List<String> functionParams;
    private String dimension;


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
