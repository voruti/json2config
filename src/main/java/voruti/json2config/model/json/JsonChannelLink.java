package voruti.json2config.model.json;

import com.google.gson.Gson;
import lombok.Getter;
import voruti.json2config.model.IConvertible;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class JsonChannelLink implements IConvertible {

    private static final Gson GSON = new Gson();
    private Value value;


    @Override
    public String toConfigLine(String lineBefore) {
        // first channel or append:
        String format = "channel=\"%s\"%s}";
        if (lineBefore.endsWith("}")) {
            lineBefore = lineBefore.substring(0, lineBefore.length() - 1);
            format = "%s, " + format;
        } else {
            format = "%s {" + format;
        }

        // profile:
        String propertiesString = "";
        if (value.configuration.properties != null) {
            String profile = value.configuration.properties.get("profile");
            if (profile != null && !profile.equals("system:default")) {
                propertiesString = String.format("[%s]",
                        value.configuration.properties.entrySet().stream()
                                .map(propertiesEntry -> String.format("%s=%s", propertiesEntry.getKey(), GSON.toJson(propertiesEntry.getValue())))
                                .collect(Collectors.joining(", "))
                );
            }
        }

        return String.format(format, lineBefore, String.join(":", value.channelUID.segments), propertiesString).strip();
    }


    @Getter
    public static class Value {
        private ChannelUID channelUID;
        private Configuration configuration;
        private String itemName;


        private static class ChannelUID {
            private List<String> segments;
        }

        private static class Configuration {
            private Map<String, String> properties;
        }
    }
}
