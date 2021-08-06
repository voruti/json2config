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
    public String toConfigLine(String name) {
        return null;
    }


    @Getter
    @ToString
    public static class Value {
        private ChannelUID channelUID;
        private Configuration configuration;
        private String itemName;


        @Getter
        @ToString
        public static class ChannelUID {
            private List<String> segments;
        }

        @Getter
        @ToString
        public static class Configuration {
            private Properties properties;

            @Getter
            @ToString
            public static class Properties {
                private String profile;
            }
        }
    }
}
