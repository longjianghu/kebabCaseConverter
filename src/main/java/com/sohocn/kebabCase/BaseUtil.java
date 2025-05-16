package com.sohocn.kebabCase;

public class BaseUtil {
    public static String convertToKebabCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        char prevChar = '\0';

        for (char c : text.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (prevChar != '\0' && (Character.isLowerCase(prevChar) || Character.isDigit(prevChar))) {
                    result.append('-');
                }
                result.append(Character.toLowerCase(c));
                prevChar = c;
            } else if (Character.isLowerCase(c) || Character.isDigit(c)) {
                result.append(c);
                prevChar = c;
            } else if (c == '_' || Character.isWhitespace(c)) {
                if (prevChar != '-' && prevChar != '\0') {
                    result.append('-');
                }
                prevChar = '-';
            } else if (c == '-') {
                if (prevChar != '-') {
                    result.append(c);
                }
                prevChar = c;
            } else {
                if (prevChar != '-' && prevChar != '\0') {
                    result.append('-');
                }
                prevChar = '-';
            }
        }

        String str = result.toString();

        if (str.startsWith("-")) {
            str = str.substring(1);
        }
        if (str.endsWith("-")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

}
