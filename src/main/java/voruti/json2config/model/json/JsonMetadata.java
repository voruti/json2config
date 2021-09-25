package voruti.json2config.model.json;

import lombok.Getter;
import voruti.json2config.model.IAppendable;

import java.util.List;

@Getter
public class JsonMetadata implements IAppendable {

    private Value value;


    @Override
    public String toConfigLine(String lineBefore) {
        // first metadata or append:
        String format = "%s=\"%s\"}";
        if (lineBefore.endsWith("}")) {
            lineBefore = lineBefore.substring(0, lineBefore.length() - 1);
            format = "%s, " + format;
        } else {
            format = "%s {" + format;
        }
        return String.format(format, lineBefore, value.key.segments.get(0), value.value).strip();
    }

    @Override
    public String getItemName() {
        return value.key.segments.get(1);
    }


    @Getter
    public static class Value {
        private Key key;
        private String value;

        private static class Key {
            private List<String> segments;
        }
    }
}
