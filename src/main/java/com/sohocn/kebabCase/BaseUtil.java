package com.sohocn.kebabCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Base util.
 *
 * @author longjianghu
 */
public class BaseUtil {
    /**
     * Convert to kebab case string.
     *
     * @param text
     *            the text
     * @return the string
     */
    public static String convertToKebabCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        Pattern pattern = Pattern.compile("(?<=[a-z0-9])([A-Z])");
        Matcher matcher = pattern.matcher(text);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            matcher.appendReplacement(sb, "-" + matcher.group(1).toLowerCase());
        }

        matcher.appendTail(sb);

        return sb.toString().toLowerCase();
    }
}
