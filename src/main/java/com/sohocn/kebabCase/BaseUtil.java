package com.sohocn.kebabCase;

import java.util.regex.Pattern;

/**
 * The type Base util.
 *
 * @author longjianghu
 */
public class BaseUtil {
    /**
     * 下划线转连字符的正则
     */
    private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_");
    /**
     * 检测是否已经是kebab-case (只包含小写字母、数字和连字符，且连字符不以连续形式出现)
     */
    private static final Pattern KEBAB_CASE_PATTERN = Pattern.compile("^[a-z0-9]+(-[a-z0-9]+)*$");

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

        // 检测是否已经是kebab-case
        if (KEBAB_CASE_PATTERN.matcher(text).matches()) {
            return text;
        }

        // 1. 先将下划线转换为连字符
        String result = UNDERSCORE_PATTERN.matcher(text).replaceAll("-");

        // 2. 处理各种边界情况，插入连字符
        // 使用更精确的模式匹配

        // 2a. 在小写/数字后跟大写字母时插入连字符 (userName -> user-Name)
        result = result.replaceAll("([a-z0-9])([A-Z])", "$1-$2");

        // 2b. 在大写字母后跟另一组大写字母再跟小写字母时插入连字符
        // 例如: XMLHTTPRequest -> XML-HTTPRequest (在L和H之间)
        // 但要避免: XMLParser -> X-MLParser (错误)
        // 策略: 找到"大写+大写+小写"的模式，在前面两个大写之间插入连字符
        // 但只在前一个大写字母序列长度>1时
        // 使用负向前瞻来确保不会过度分割
        result = result.replaceAll("([A-Z]{2,})([A-Z])(?=[a-z])", "$1-$2");

        // 3. 转换为小写
        result = result.toLowerCase();

        // 4. 清理多余的连字符
        result = result.replaceAll("-+", "-");

        // 5. 移除首尾连字符
        result = result.replaceAll("^-|-$", "");

        return result;
    }
}
