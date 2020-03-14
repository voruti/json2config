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
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author voruti
 */
public class Converter {

	public enum Type {
		ITEM, THING
	}

	private static final String CLASS_NAME = Converter.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLASS_NAME);

	/**
	 * Converts {@code jsonFile} to {@code outputFile}.
	 * 
	 * @param jsonFile   path to file (input)
	 * @param outputFile path to file (output)
	 * @param type       type of file to convert
	 */
	public Converter(String jsonFile, String outputFile, Type type) {
		LOGGER.entering(CLASS_NAME, "<init>", jsonFile);

		// gets the jsonObject:
		JSONObject jsonObject = openFileToJSONObject(jsonFile);
		// converts first elements to map of IConvertibles:
		Map<String, IConvertible> convertibleMap = goThroughFirstEntrysOfJSONObject(jsonObject, type);
		// get lines from map:
		List<String> lines = convertibleMapToLines(convertibleMap);
		// write file:
		writeLinesToFile(lines, outputFile);

		LOGGER.exiting(CLASS_NAME, "<init>");
	}

	/**
	 * Opens and reads file {@code fileName} and returns it content as
	 * {@link JSONObject}.
	 * 
	 * @param fileName the file to open and read
	 * @return the {@link JSONObject} containing the content of the file, if it
	 *         could be successfully opened, otherwise {@code null}
	 */
	public JSONObject openFileToJSONObject(String fileName) {
		LOGGER.entering(CLASS_NAME, "openFileToJSONObject", fileName);

		JSONObject jsonObject = null;

		File file = new File(fileName);
		String str = "";
		Scanner sc;
		try {
			sc = new Scanner(file);

			LOGGER.log(Level.INFO, "Reading lines of file={0} with Scanner={1}", new Object[] { file, sc });
			while (sc.hasNextLine()) {
				str += sc.nextLine();
			}
			sc.close();

			jsonObject = new JSONObject(str);
		} catch (JSONException eJ) {
			LOGGER.log(Level.SEVERE, "File content={0} can not be parsed to JSONObject", str);
			eJ.printStackTrace();
		} catch (FileNotFoundException eF) {
			LOGGER.log(Level.SEVERE, "File can not be opened!", file);
			eF.printStackTrace();
		}

		LOGGER.exiting(CLASS_NAME, "openFileToJSONObject", jsonObject);
		return jsonObject;
	}

	/**
	 * Creates a {@link Map} of all "first values" of the {@code jsonObject}. Uses
	 * {@code type} to determine in which {@link Type} to convert the
	 * {@code jsonObject} entries.
	 * 
	 * @param jsonObject the {@link JSONObject} to convert
	 * @param type       the {@link Type} in which to convert the {@code jsonObject}
	 *                   entries
	 * @return a {@link Map} containing the {@link IConvertible IConvertibles} of
	 *         the {@code jsonObject}; contains no entries if {@code jsonObject} is
	 *         {@code null} (or wrong {@code type} is found)
	 */
	public Map<String, IConvertible> goThroughFirstEntrysOfJSONObject(JSONObject jsonObject, Type type) {
		LOGGER.entering(CLASS_NAME, "goThroughFirstEntrysOfJSONObject", new Object[] { jsonObject, type });

		Map<String, IConvertible> returnVal = new HashMap<>();

		if (jsonObject != null) {
			Iterator<String> ite = jsonObject.keys();
			while (ite.hasNext()) {
				String key = ite.next();

				Object o = jsonObject.get(key);
				if (!(o instanceof JSONObject)) {
					LOGGER.log(Level.SEVERE, "Value ({0}) should be instanceof JSONObject, but is not!", o);
					break;
				}
				JSONObject val = (JSONObject) o;

				IConvertible iconv = null;
				if (type == Type.ITEM) {
					iconv = createItem(val);
				} else if (type == Type.THING) {
					// iconv = createThing(val);
				} else {
					LOGGER.log(Level.SEVERE, "Wrong type={0}", type);
					break;
				}

				LOGGER.log(Level.INFO, "Adding IConvertible={0} to convertiblesMap", iconv);
				returnVal.put(key, iconv);
			}
		} else {
			LOGGER.log(Level.WARNING, "jsonObject is null");
		}

		LOGGER.exiting(CLASS_NAME, "goThroughFirstEntrysOfJSONObject", returnVal);
		return returnVal;
	}

	/**
	 * Creates a {@link Item} out of a {@link JSONObject}.
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
	 * Converts the {@code map} with objects of {@link IConvertible} implementing
	 * classes into {@link String} lines in form of a {@link List}.
	 * 
	 * @param map the map to convert
	 * @return a {@link List} with all lines as {@link String Strings}
	 */
	public List<String> convertibleMapToLines(Map<String, IConvertible> map) {
		LOGGER.entering(CLASS_NAME, "convertibleMapToLines", map);

		List<String> newLines = new ArrayList<>();

		if (map.size() > 0) {
			List<String> lines = new ArrayList<>();

			for (String key : map.keySet()) {
				IConvertible conv = map.get(key);
				LOGGER.log(Level.FINE, "Generating line for {0}", String.format("%1$s: %2$s", key, conv));
				String line = conv.toConfigLine(key);
				LOGGER.log(Level.INFO, "Created line=[{0}]", line);
				lines.add(line);
			}

			lines.sort(Comparator.naturalOrder());

			// adding empty lines between:
			String last = lines.get(0).substring(0, 4);
			for (String line : lines) {
				String now = line.substring(0, 4);
				if (!now.equalsIgnoreCase(last)) {
					newLines.add("");
				}
				newLines.add(line);

				last = now;
			}
		} else {
			LOGGER.log(Level.WARNING, "No objects in map={0}", map);
		}

		LOGGER.exiting(CLASS_NAME, "convertibleMapToLines", newLines);
		return newLines;
	}

	/**
	 * Writes every entry of {@code lines} in a separate line to {@code fileName}.
	 * 
	 * @param lines    the lines to write into the file
	 * @param fileName the file name of the file to write
	 * @return {@code true} if the writing operation was successful, {@code false}
	 *         otherwise
	 */
	public boolean writeLinesToFile(List<String> lines, String fileName) {
		LOGGER.entering(CLASS_NAME, "writeLinesToFile", fileName);

		boolean returnVal;

		if (!lines.isEmpty()) {
			// writing to file:
			try {
				LOGGER.log(Level.INFO, "Writing lines to file={0}", fileName);
				Files.write(Paths.get(fileName), lines, Charset.defaultCharset());
				returnVal = true;
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "{0} at writing file with lines={1}", new Object[] { e.toString(), lines });
				e.printStackTrace();
				returnVal = false;
			}
		} else {
			LOGGER.log(Level.WARNING, "No objects in List lines={0}", lines);
			returnVal = false;
		}

		LOGGER.exiting(CLASS_NAME, "writeLinesToFile", returnVal);
		return returnVal;
	}

}
