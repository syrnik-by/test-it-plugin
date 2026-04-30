package ru.testit.management.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern CAMEL_REGEX = Pattern.compile("(?<=[a-zA-Z])[A-Z]");
    private static final Pattern SNAKE_REGEX = Pattern.compile("_[a-zA-Z]");

    private StringUtils() {}

    private static String camelToSnakeCase(String str) {
        Matcher matcher = CAMEL_REGEX.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group());
        }
        matcher.appendTail(sb);
        return sb.toString().toLowerCase(Locale.getDefault());
    }

    private static String snakeToLowerCamelCase(String str) {
        Matcher matcher = SNAKE_REGEX.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group().replace("_", "").toUpperCase(Locale.getDefault()));
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.isEmpty()) return result;
        return Character.toLowerCase(result.charAt(0)) + result.substring(1);
    }

    private static String snakeToUpperCamelCase(String str) {
        String lower = snakeToLowerCamelCase(str);
        if (lower.isEmpty()) return lower;
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    public static String spacesToSnakeCase(String str) {
        return str.replace(" ", "_");
    }

    public static String spacesToCamelCase(String str) {
        return snakeToLowerCamelCase(spacesToSnakeCase(str));
    }
}