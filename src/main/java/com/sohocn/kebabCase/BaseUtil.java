package com.sohocn.kebabCase;

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
            return text;
        }

        // 1. 替换所有空白字符（包括空格、\t、\n等）为中横线
        text = text.replaceAll("\\s+", "-");

        // 2. 替换下划线 _ 为中横线
        text = text.replace("_", "-");

        // 3. 处理驼峰命名（camelCase/PascalCase）
        text = text.replaceAll("(?<=[a-z])[A-Z]|[A-Z](?=[a-z])", "-$0");

        // 4. 全部转小写
        text = text.toLowerCase();

        // 5. 合并连续的 -
        text = text.replaceAll("-+", "-");

        // 6. 去除开头和结尾的 -
        text = text.replaceAll("^-|-$", "");

        return text;
    }

}
