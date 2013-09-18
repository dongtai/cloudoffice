package apps.transmanager.weboffice.service.mail;



public class StringUtil {
	/**
	 * 将字符串src中的子字符串fnd全部替换为新子字符串rep.<br>
	 * 功能相当于java sdk 1.4的String.replaceall方法.<br>
	 * 不同之处在于查找时不是使用正则表达式而是普通字符串.
	 */
	public static String replaceall(String src, String fnd, String rep)
			throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;

		int idx = dst.indexOf(fnd);

		while (idx >= 0) {
			dst = dst.substring(0, idx) + rep
					+ dst.substring(idx + fnd.length(), dst.length());
			idx = dst.indexOf(fnd, idx + rep.length());
		}

		return dst;
	}

	/**
	 * 转换为html编码.<br>
	 */
	public static String htmlencoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceall(dst, "<", "&lt;");
		dst = replaceall(dst, ">", "&rt;");
		dst = replaceall(dst, "\"", "&quot;");
		dst = replaceall(dst, "'", "&#039;");

		return dst;
	}

	/**
	 * 转换为html文字编码.<br>
	 */
	public static String htmltextencoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceall(dst, "<", "&lt;");
		dst = replaceall(dst, ">", "&gt;");
		dst = replaceall(dst, "\"", "&quot;");
		dst = replaceall(dst, "'", "&#039;");
		dst = replaceall(dst, " ", "&nbsp;");
		dst = replaceall(dst, "\r\n", "<br>");
		dst = replaceall(dst, "\r", "<br>");
		dst = replaceall(dst, "\n", "<br>");

		return dst;
	}

	/**
	 * 转换为url编码.<br>
	 */
	public static String urlencoder(String src, String enc) throws Exception {
		return java.net.URLEncoder.encode(src, enc);
	}

	/**
	 * 转换为xml编码.<br>
	 */
	public static String xmlencoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceall(dst, "&", "&amp;");
		dst = replaceall(dst, "<", "&lt;");
		dst = replaceall(dst, ">", "&gt;");
		dst = replaceall(dst, "\"", "&quot;");
		dst = replaceall(dst, "\'", "&acute;");

		return dst;
	}

	/**
	 * 转换为sql编码.<br>
	 */
	public static String sqlencoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		return replaceall(src, "'", "''");
	}

	/**
	 * 转换为javascript编码.<br>
	 */
	public static String jsencoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceall(dst, "'", "\\'");
		dst = replaceall(dst, "\"", "\\\"");
		// dst = replaceall(dst, "\r\n", "\\\n"); // 和\n转换有冲突
		dst = replaceall(dst, "\n", "\\\n");
		dst = replaceall(dst, "\r", "\\\n");

		return dst;
	}
}
