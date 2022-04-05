package tools.bestquality.util;

public interface Strings {

    static boolean isBlank(String value) {
        return value == null ||
                value.trim().length() == 0;
    }

    static boolean isNotBlank(String value) {
        return value != null &&
                value.trim().length() > 0;
    }

    static String trim(String value) {
        return value != null ? value.trim() : null;
    }
}
