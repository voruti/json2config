package voruti.json2config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author U147496
 *
 */
public class Json{

	private String prefix;

	private static final String SEPARATOR = "    ";
	private static final String FILE = "items.json";

	/**
	 * @throws FileNotFoundException
	 * 
	 */
	public MyProg() throws FileNotFoundException {
		prefix = "";

		File file = new File(FILE);
		Scanner sc = new Scanner(file);

		String str = "";
		while (sc.hasNextLine()) {
			str += sc.nextLine();
		}
		sc.close();

		auswerten(new JSONObject(str));
	}

	public void auswerten(JSONObject jso) {
		Iterator<String> ite = jso.keys();
		while (ite.hasNext()) {
			String key = ite.next();
			Object val = jso.get(key);
			if (val instanceof JSONObject) {
				System.out.println(prefix + key + "=");

				prefix += SEPARATOR;
				auswerten((JSONObject) val);
				prefix = prefix.substring(0, prefix.length() - SEPARATOR.length());
			} else if (val instanceof JSONArray) {
				System.out.println(prefix + key + " (JSONArray):");

				prefix += SEPARATOR;
				for (Object obj : (JSONArray) val) {
					System.out.println(prefix + obj + " - " + obj.getClass().getSimpleName());
				}
				prefix = prefix.substring(0, prefix.length() - SEPARATOR.length());
			} else {
				System.out.println(prefix + key + ": " + val + " - " + val.getClass().getSimpleName());
			}
		}
	}

}
