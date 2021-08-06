package voruti.json2config.model.json;

import lombok.Getter;
import lombok.ToString;
import voruti.json2config.model.IConvertible;

import java.util.List;

@Getter
@ToString
public class JsonChannelLink implements IConvertible {

    private Value value;


    @Override
    public String toConfigLine(String lineBefore) {
        return String.format("%s {channel=\"%s\"}", lineBefore, String.join(":", value.channelUID.segments)).strip();
    }


    @Getter
    @ToString
    public static class Value {
        private ChannelUID channelUID;
        private Configuration configuration;
        private String itemName;


        @ToString
        private static class ChannelUID {
            private List<String> segments;
        }

        @ToString
        private static class Configuration {
            private Properties properties;

            @ToString
            private static class Properties {
                private String profile;
            }
        }
    }
}
