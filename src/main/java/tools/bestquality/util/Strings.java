package tools.bestquality.util;

public class Strings {

    public static boolean isBlank(String value) {
        return value == null ||
                value.trim().length() == 0;
    }

    public static boolean isNotBlank(String value) {
        return value != null &&
                value.trim().length() > 0;
    }

    public static String trim(String value) {
        return value != null ? value.trim() : null;
    }
}
