package voruti.json2config;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.smarthome.core.items.ManagedItemProvider.PersistedItem;

/**
 * @author voruti
 *
 */
public class MyItem extends PersistedItem {

	/**
	 * @param itemType
	 */
	public MyItem(String itemType) {
		super(itemType);
	}

	@Override
	public String toString() {
		final int maxLen = 10;
		return String.format(
				"MyItem [baseItemType=%-8.8s, groupNames=%-20.20s, itemType=%-10.10s, tags=%-5.5s, label=%-15.15s, category=%-15.15s, functionName=%-10.10s, functionParams=%-5.5s, dimension=%-10.10s]",
				baseItemType, groupNames != null ? toString(groupNames, maxLen) : null, itemType,
				tags != null ? toString(tags, maxLen) : null, label, category, functionName,
				functionParams != null ? toString(functionParams, maxLen) : null, dimension);
	}

	private String toString(Collection<?> collection, int maxLen) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		int i = 0;
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
			if (i > 0) {
				builder.append(", ");
			}
			builder.append(iterator.next());
		}
		builder.append("]");
		return builder.toString();
	}

}
