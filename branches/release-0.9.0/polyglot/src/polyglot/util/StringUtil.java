package polyglot.util;

/** String utilities. */
public class StringUtil
{
    /**
     * Given the name for a class, returns the portion which appears to
     * constitute the package -- i.e., all characters up to but not including
     * the last dot, or no characters if the name has no dot.
     **/
    public static String getPackageComponent(String fullName) {
	int lastDot = fullName.lastIndexOf('.');
	return lastDot >= 0 ? fullName.substring(0,lastDot) : "";
    }
   
    /**
     * Given the name for a class, returns the portion which appears to
     * constitute the package -- i.e., all characters after the last
     * dot, or all the characters if the name has no dot.
     **/
    public static String getShortNameComponent(String fullName) {
	int lastDot = fullName.lastIndexOf('.');
	return lastDot >= 0 ? fullName.substring(lastDot+1) : fullName;
    }

    /**
     * Returns true iff the provided class name does not appear to be
     * qualified (i.e., it has no dot.)
     **/
    public static boolean isNameShort(String name) {
	return name.indexOf('.') < 0;
    }

    public static String getFirstComponent(String fullName) {
	int firstDot = fullName.indexOf('.');
	return firstDot >= 0 ? fullName.substring(0,firstDot) : fullName;
    }

    public static String removeFirstComponent(String fullName) {
	int firstDot = fullName.indexOf('.');
	return firstDot >= 0 ? fullName.substring(firstDot+1) : "";
    }
 
    public static String escape(String s) {
        StringBuffer sb = new StringBuffer(s.length());

	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    escape(sb, c);
	}

	return sb.toString();
    }

    public static String escape(char c) {
        return escape("" + c);
    }

    private static void escape(StringBuffer sb, char c) {
        if (c > 0xff) {
            sb.append(c);
	    return;
	}

	switch (c) {
	    case '\b': sb.append("\\b"); return;
	    case '\t': sb.append("\\t"); return;
	    case '\n': sb.append("\\n"); return;
	    case '\f': sb.append("\\f"); return;
	    case '\r': sb.append("\\r"); return;
	    case '\"': sb.append("\\" + c); return; // "\\\"";
	    case '\'': sb.append("\\" + c); return; // "\\\'";
	    case '\\': sb.append("\\" + c); return; // "\\\\";
	}

        if (c >= 0x20 && c < 0x7f) {
            sb.append(c);
	    return;
	}

        sb.append("\\" + (char) ('0' + c / 64)
                       + (char) ('0' + (c & 63) / 8)
                       + (char) ('0' + (c & 7)));
    }
}
