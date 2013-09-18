package apps.transmanager.weboffice.util.beans;

import org.apache.commons.lang.xwork.StringEscapeUtils;

public class SQLUtil {

	public static String stringEscape(String key) {
		return StringEscapeUtils.escapeSql(key).replace("%", "\\%").replace("_", "\\_").replace("[", "\\[");
	}
}
