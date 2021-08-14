package voruti.json2config.model.json;

import voruti.json2config.model.IConvertible;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class JsonItem implements IConvertible {

    private Value value;


    @Override
    public String toConfigLine(String name) {
        String baseItemTypeString = value.baseItemType == null || value.baseItemType.isEmpty()
                ? ""
                : ":" + value.baseItemType;
        String functionNameString = value.functionName == null || value.functionName.isEmpty()
                ? ""
                : ":" + value.functionName;
        String functionParamsString = value.functionParams == null || value.functionParams.isEmpty()
                ? ""
                : String.format("(%s)", String.join(",", value.functionParams));
        String beginString = value.itemType == null ? "" : value.itemType;
        if (value.itemType != null) {
            if (value.itemType.equalsIgnoreCase("Group")) {
                beginString += baseItemTypeString + functionNameString + functionParamsString;
            } else if (value.dimension != null && !value.itemType.contains(":")) {
                beginString += value.dimension;
            }
        }

        String labelString = value.label == null || value.label.isEmpty()
                ? ""
                : String.format("\"%s\"", value.label);

        String categoryString = value.category == null || value.category.isEmpty()
                ? ""
                : String.format("<%s>", value.category.toLowerCase());

        String groupNamesString = value.groupNames == null || value.groupNames.isEmpty()
                ? ""
                : String.format("(%s)", String.join(", ", value.groupNames));

        String tagsString = value.tags == null || value.tags.isEmpty()
                ? ""
                : String.format("[\"%s\"]", String.join("\", \"", value.tags));

        return new StringJoiner(" ")
                .add(beginString)
                .add(name)
                .add(labelString)
                .add(categoryString)
                .add(groupNamesString)
                .add(tagsString)
                .toString().strip();
    }


    private static class Value {
        private String baseItemType;
        private List<String> groupNames;
        private String itemType;
        private Set<String> tags;
        private String label;
        private String category;
        private String functionName;
        private List<String> functionParams;
        private String dimension;
    }
}
