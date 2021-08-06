package voruti.json2config.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author voruti
 */
@Getter
@Setter
@ToString
public class Channel implements IConvertible {

    private String itemName;
    private String channelUID;


    @Override
    public String toConfigLine(String name) {
        return "";
    }
}