package voruti.json2config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * @author voruti
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class Item implements IConvertible {

    private String name;
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
     * @return a item config line
     */
    @Override
    public String toConfigLine() {
        String baseItemTypeString = baseItemType.isEmpty()
                ? ""
                : ":" + baseItemType;
        String functionNameString = functionName.isEmpty()
                ? ""
                : ":" + functionName;
        String functionParamsString = functionParams.isEmpty()
                ? ""
                : String.format("(%s)", String.join(",", functionParams));
        String beginString = itemType;
        if (itemType.equalsIgnoreCase("Group")) {
            beginString += baseItemTypeString + functionNameString + functionParamsString;
        }

        String labelString = label.isEmpty()
                ? ""
                : String.format("\"%s\"", label);

        String categoryString = category.isEmpty()
                ? ""
                : String.format("<%s>", category.toLowerCase());

        String groupNamesString = groupNames.isEmpty()
                ? ""
                : String.format("(%s)", String.join(", ", groupNames));

        String tagsString = tags.isEmpty()
                ? ""
                : String.format("[%s]", String.join(", ", tags));

        return new StringJoiner(" ")
                .add(beginString)
                .add(name)
                .add(labelString)
                .add(categoryString)
                .add(groupNamesString)
                .add(tagsString)
                .toString().strip();
    }
}
