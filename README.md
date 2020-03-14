# JSON2Config
 
A simple utility program which converts [openHAB Items](https://www.openhab.org/docs/configuration/items.html) from [JsonDB Storage](https://www.openhab.org/docs/administration/jsondb.html) files (e.g. org.eclipse.smarthome.core.items.Item.json) to textual configuration files (e.g. my.items).



## Usage

You can run the tool with Java:
```bash
java -jar json2config-XXX.jar [arguments]
```
The program has two main features:


### 1. Converting org.eclipse.smarthome.core.items.Item.json into a *.items file



### 2. Appending channel links from org.eclipse.smarthome.core.thing.link.ItemChannelLink.json to *.items files


