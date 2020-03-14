# JSON2Config
 
A simple utility program which converts [openHAB Items](https://www.openhab.org/docs/configuration/items.html) from [JsonDB Storage](https://www.openhab.org/docs/administration/jsondb.html) files (e.g. org.eclipse.smarthome.core.items.Item.json) to textual configuration files (e.g. my.items).



## Usage

You can run the tool with Java:
```bash
java -jar json2config-XXX.jar [arguments]
```
The program has two main features:


### 1. Converting org.eclipse.smarthome.core.items.Item.json into a *.items file

To convert the JSON file, you can simply put this tool into the [JSON DB storage location](https://www.openhab.org/docs/administration/jsondb.html#storage-location) and run it. This will create a `json.items` file with all the items from the `org.eclipse.smarthome.core.items.Item.json` file.
Alternatively you can adjust the program arguments to specify the file locations:
- The `-i <path>`/`--in <path>`/`--json <path>` parameters allow you to specify the input / .json file.
- The 

### 2. Appending channel links from org.eclipse.smarthome.core.thing.link.ItemChannelLink.json to *.items files


