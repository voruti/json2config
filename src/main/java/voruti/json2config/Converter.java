package voruti.json2config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author voruti
 */
public class Converter {

	private static final String SEPARATOR = "    ";
	private static final String CLASS_NAME = Converter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	private Map<String, Item> itemsMap;
	private String prefix;

	/**
	 * Converts {@code jsonFile} to {@code itemsFile}.
	 * 
	 * @param jsonFile  path to file (input)
	 * @param itemsFile path to file (output)
	 */
	public Converter(String jsonFile, String itemsFile) {
		LOGGER.entering(CLASS_NAME, "<init>", jsonFile);

		itemsMap = new HashMap<>();
		prefix = "";

		File file = new File(jsonFile);
		Scanner sc;
		try {
			sc = new Scanner(file);

			LOGGER.log(Level.INFO, "Reading lines of file {0}", file);
			String str = "";
			while (sc.hasNextLine()) {
				str += sc.nextLine();
			}
			sc.close();

			JSONObject jsonObject = new JSONObject(str);

//			LOGGER.log(Level.INFO, "Starting evaluating file {0} with JSONObject {1}",
//					new Object[] { file, jsonObject });
//			auswerten(jsonObject);

			Iterator<String> ite = jsonObject.keys();
			while (ite.hasNext()) {
				String key = ite.next();

				Object o = jsonObject.get(key);
				if (!(o instanceof JSONObject)) {
					LOGGER.log(Level.SEVERE, "Value ({0}) should be instanceof JSONObject, but is not!", o);
					break;
				}
				JSONObject val = (JSONObject) o;

				Item item = createItem(val);
				LOGGER.log(Level.INFO, "Adding item={0} to itemsMap", item);
				itemsMap.put(key, item);
			}

			writeItems(itemsFile);
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, "File can not be opened!", file);
			e.printStackTrace();
		}

		LOGGER.exiting(CLASS_NAME, "<init>");
	}

	/**
	 * Go through the whole {@link JSONObject} in "tree form".
	 * 
	 * @param jso the {@link JSONObject}
	 */
	public void auswerten(JSONObject jso) {
		LOGGER.entering(CLASS_NAME, "auswerten", jso);

		Iterator<String> ite = jso.keys();
		while (ite.hasNext()) {
			String key = ite.next();
			Object val = jso.get(key);
			if (val instanceof JSONObject) {
				LOGGER.log(Level.INFO, "Found another JSONObject {0}: recursion", val);
				System.out.println(prefix + key + "=");

				prefix += SEPARATOR;
				auswerten((JSONObject) val);
				prefix = prefix.substring(0, prefix.length() - SEPARATOR.length());
			} else if (val instanceof JSONArray) {
				LOGGER.log(Level.INFO, "Found a JSONArray {0}", val);
				System.out.println(prefix + key + " (JSONArray):");

				prefix += SEPARATOR;
				for (Object obj : (JSONArray) val) {
					LOGGER.log(Level.FINE, "Printing array value {0}", obj);
					System.out.println(prefix + obj + " - " + obj.getClass().getSimpleName());
				}
				prefix = prefix.substring(0, prefix.length() - SEPARATOR.length());
			} else {
				LOGGER.log(Level.FINE, "Printing plain value {0}", val);
				System.out.println(prefix + key + ": " + val + " - " + val.getClass().getSimpleName());
			}
		}

		LOGGER.exiting(CLASS_NAME, "auswerten");
	}

	/**
	 * Creates a {@link {@link JSONObject}} out of a {@link JSONObject}.
	 * 
	 * @param content the {@link JSONObject}
	 * @return the item as {@link Item}
	 */
	public Item createItem(JSONObject content) {
		LOGGER.entering(CLASS_NAME, "createItem", content);

		String itemType = "";
		String label = "";
		String category = "";
		String baseItemType = "";
		String functionName = "";
		List<String> groupNames = new ArrayList<>();
		Set<String> tags = new HashSet<>();
		List<String> functionParams = new ArrayList<>();
		String dimension = "";

		Iterator<String> ite1 = content.keys();
		while (ite1.hasNext()) {
			String key1 = ite1.next();
			Object val1 = content.get(key1);

			switch (key1) {
			case "class":
				if (!val1.equals("org.eclipse.smarthome.core.items.ManagedItemProvider$PersistedItem")) {
					LOGGER.log(Level.WARNING, "class={0} different than expected!", val1);
				}
				break;
			case "value":
				if (val1 instanceof JSONObject) {
					JSONObject jso2 = (JSONObject) val1;
					Iterator<String> ite2 = jso2.keys();
					while (ite2.hasNext()) {
						String key2 = ite2.next();
						Object val2 = jso2.get(key2);

						switch (key2) {
						case "itemType":
							if (val2 instanceof String) {
								itemType = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;
						case "label":
							if (val2 instanceof String) {
								label = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;
						case "category":
							if (val2 instanceof String) {
								category = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;
						case "baseItemType":
							if (val2 instanceof String) {
								baseItemType = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;
						case "functionName":
							if (val2 instanceof String) {
								functionName = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;
						case "groupNames":
							if (val2 instanceof JSONArray) {
								for (Object o : (JSONArray) val2) {
									if (o instanceof String) {
										groupNames.add((String) o);
									} else {
										LOGGER.log(Level.WARNING, "JSONArray={0} item={1} is not instanceof String!",
												new Object[] { key2, o });
									}
								}
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONArray!",
										new Object[] { key2, val2 });
							}
							break;
						case "tags":
							if (val2 instanceof JSONArray) {
								for (Object o : (JSONArray) val2) {
									if (o instanceof String) {
										tags.add((String) o);
									} else {
										LOGGER.log(Level.WARNING, "JSONArray={0} item={1} is not instanceof String!",
												new Object[] { key2, o });
									}
								}
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONArray!",
										new Object[] { key2, val2 });
							}
							break;
						case "functionParams":
							if (val2 instanceof JSONArray) {
								for (Object o : (JSONArray) val2) {
									if (o instanceof String) {
										functionParams.add((String) o);
									} else {
										LOGGER.log(Level.WARNING, "JSONArray={0} item={1} is not instanceof String!",
												new Object[] { key2, o });
									}
								}
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONArray!",
										new Object[] { key2, val2 });
							}
							break;
						case "dimension":
							if (val2 instanceof String) {
								dimension = (String) val2;
							} else {
								LOGGER.log(Level.WARNING, "{0}={1} is not instanceof String!",
										new Object[] { key2, val2 });
							}
							break;

						default:
							LOGGER.log(Level.WARNING, "Unexpected key={0}", key2);
							break;
						}
					}
					break;
				} else {
					LOGGER.log(Level.WARNING, "{0}={1} is not instanceof JSONObject!", new Object[] { key1, val1 });
				}

			default:
				LOGGER.log(Level.WARNING, "Unexpected key={0}", key1);
				break;
			}
		}

		Item item = new Item(itemType);
		item.category = category;
		item.label = label;
		item.baseItemType = baseItemType;
		item.functionName = functionName;
		item.groupNames = groupNames;
		item.tags = tags;
		item.functionParams = functionParams;
		item.dimension = dimension;

		LOGGER.exiting(CLASS_NAME, "createItem", item);
		return item;
	}

	/**
	 * Writes the items into file {@code itemsFile}.
	 * 
	 * @param itemsFile the file to write to
	 */
	public void writeItems(String itemsFile) {
		LOGGER.entering(CLASS_NAME, "writeItems");

		if (itemsMap.size() > 0) {
			List<String> lines = new ArrayList<>();

			for (String key : itemsMap.keySet()) {
				Item item = itemsMap.get(key);
				LOGGER.log(Level.FINE, "Generating line for {0}", String.format("%1$s: %2$s", key, item));
				String line = item.toItemConfig(key);
				LOGGER.log(Level.INFO, "Created line=[{0}]", line);
				lines.add(line);
			}

			lines.sort(Comparator.naturalOrder());

			// adding empty lines between:
			List<String> newLines = new ArrayList<>();
			String last = lines.get(0).substring(0, 4);
			for (String line : lines) {
				String now = line.substring(0, 4);
				if (!now.equalsIgnoreCase(last)) {
					newLines.add("");
				}
				newLines.add(line);

				last = now;
			}

			// writing to file:
			try {
				LOGGER.log(Level.INFO, "Writing lines to file={0}", itemsFile);
				Files.write(Paths.get(itemsFile), newLines, Charset.defaultCharset());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "{0} at writing file with lines={1}", new Object[] { e.toString(), newLines });
				e.printStackTrace();
			}
		} else {
			LOGGER.log(Level.WARNING, "No items to print");
		}

		LOGGER.exiting(CLASS_NAME, "writeItems");
	}

}
