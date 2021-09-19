package voruti.json2config.service;

public final class Constants {

    // logging constants:
    public static final String LOG_CANT_OPEN_FILE = "Can't open file {}";

    // default voruti.json2config.Starter argument values:
    public static final String DEFAULT_V2_JSON_FILE = "org.eclipse.smarthome.core.items.Item.json";
    public static final String DEFAULT_V3_JSON_FILE = "org.openhab.core.items.Item.json";
    public static final String DEFAULT_V2_CHANNEL_FILE = "org.eclipse.smarthome.core.thing.link.ItemChannelLink.json";
    public static final String DEFAULT_V3_CHANNEL_FILE = "org.openhab.core.thing.link.ItemChannelLink.json";
    public static final String DEFAULT_V2_METADATA_FILE = "org.eclipse.smarthome.core.items.Metadata.json";
    public static final String DEFAULT_V3_METADATA_FILE = "org.openhab.core.items.Metadata.json";


    private Constants() {
    }
}
