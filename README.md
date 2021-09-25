# JSON2Config

A simple utility program which converts [openHAB Items](https://www.openhab.org/docs/configuration/items.html)
from [JsonDB Storage](https://www.openhab.org/docs/administration/jsondb.html) files (e.g.
org.eclipse.smarthome.core.items.Item.json, org.eclipse.smarthome.core.thing.link.ItemChannelLink.json) to textual
configuration files (e.g. my.items).

## Usage

You can run the tool with Java:

```bash
java -jar json2config-XXX.jar [arguments]
```

The program has three main features. By default, only the first one will be executed.

### 1. Converting org.eclipse.smarthome.core.items.Item.json into a *.items file

To convert the JSON file, you can simply put this tool into
the [JSON DB storage location](https://www.openhab.org/docs/administration/jsondb.html#storage-location) and run it.
This will create a `json.items` file with all the items from the `org.eclipse.smarthome.core.items.Item.json` file.
Alternatively you can adjust the program arguments to specify the file locations:

- The `-i <path>`/`--in <path>`/`--json <path>` parameters allow you to specify the input .json file.
- The `-o <path>`/`--out <path>` parameters allow you to specify the output file.
- The `-n` parameter allows you to disable this (converting) feature completely; if you want to only append the channel
  links.

### 2. Appending channel links from org.eclipse.smarthome.core.thing.link.ItemChannelLink.json to *.items files

To append channel links to *already existing* .items files, you can use the second feature. If you already have
some `*.items` files in your configuration, it's recommended to also including these files i.e. moving them into the
same directory. By default, (when the appending feature is enabled!!) the tool will use channel links from
the `org.eclipse.smarthome.core.thing.link.ItemChannelLink.json` file and append them to all `*.items` files in the same
directory. Alternatively you can adjust the program arguments to specify the file locations:

- The `-c`/`--channel`/`--create-channel-links` parameters enable the appending feature. Without one of these, this
  feature won't run!
- The `--channel-file <path>`/`--channel-link-file <path>` parameters allow you to specify the .json file location
  containing the channel links.
- The `-d <path>`/`--dir <path>`/`--directory <path>` parameters allow you to specify the directory in which to search
  for *.items files.

### 3. Appending metadata from org.eclipse.smarthome.core.items.Metadata.json to *.items files

To append metadata to *already existing* .items files, you can use the third feature. If you already have some `*.items`
files in your configuration, it's recommended to also including these files i.e. moving them into the same directory. By
default, (when the appending feature is enabled!!) the tool will use metadata from
the `org.eclipse.smarthome.core.items.Metadata.json` file and append them to all `*.items` files in the same directory.
Alternatively you can adjust the program arguments to specify the file locations:

- The `-m`/`--metadata`/`--append-metadata` parameters enable the appending metadata feature. Without one of these, this
  feature won't run!
- The `--metadata-file <path>` parameter allows you to specify the .json file location containing the metadata.
- The `-d <path>`/`--dir <path>`/`--directory <path>` parameters allow you to specify the directory in which to search
  for *.items files.

**IMPORTANT NOTE:** Metadata attributes, found in item files as `[ roomHint="Living Room" ]` and in the metadata json
under the `configuration` key are not currently supported and will be ignored.

#### Other program features

When enabling all features, the converting feature will run first, so the appending features can then use the generated
.items file to append the channel links.

With the `-3`/`--v3`/`--openhab3` parameters default values used since openHAB version 3.X are set. Additional
parameters that specify a custom file always have priority over these defaults.
