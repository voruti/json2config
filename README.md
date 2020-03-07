# JSON2Config
 
A simple utility program which converts [OpenHAB Items](https://www.openhab.org/docs/configuration/items.html) from [JsonDB Storage](https://www.openhab.org/docs/administration/jsondb.html) files (e.g. org.eclipse.smarthome.core.items.Item.json) to textual configuration files (e.g. my.items).

## Usage

Simply run json2config.jar to convert "org.eclipse.smarthome.core.items.Item.json" (assuming it is in the same directory) to "json.items". 

You can optionally specify the input and output files:
`java -jar json2config-1.2.jar [--in <path>] [--out <path>]`


