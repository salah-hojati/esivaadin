package com.example.application.utils;
import java.util.regex.*;

public class ToFtlConverter {

    /**
     * Converts JSF/expression language patterns to FreeMarker-escaped expressions,
     * preventing double-escaping and supporting multiple prefixes
     * @param input The text containing expressions to convert
     * @param prefixes Array of expression prefixes ('#', '$', etc.)
     * @return The text with expressions properly escaped for FreeMarker
     */
    public static String convertToFtl(String input, char[] prefixes) {
        if (input == null) {
            return "";
        }
        if (prefixes == null || prefixes.length == 0) {
            throw new IllegalArgumentException("At least one prefix must be provided");
        }

        // First check if the text is already escaped for any prefix
        for (char prefix : prefixes) {
            if (input.contains("${r\"" + prefix + "{")) {
                return input; // Already escaped, return as-is
            }
        }

        // Build regex pattern for all prefixes
        String prefixPattern = buildPrefixPattern(prefixes);
        String regex = "(?<!\\$\\{r\"[" + prefixPattern + "]\\{)[" + prefixPattern + "]\\{([^}]*)\\}";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String expr = matcher.group(0);
            matcher.appendReplacement(result,
                    Matcher.quoteReplacement("${r\"" + expr + "\"}"));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    private static String buildPrefixPattern(char[] prefixes) {
        StringBuilder sb = new StringBuilder();
        for (char prefix : prefixes) {
            if (sb.length() > 0) {
                sb.append('|');
            }
            sb.append(Pattern.quote(String.valueOf(prefix)));
        }
        return sb.toString();
    }

    // Overloaded method with default '#' prefix for backward compatibility
  /*  public static String convertJsfToFtl(String input) {
        return convertJsfToFtl(input, new char[]{'#'});
    }*/

    // Example usage
    public static void main(String[] args) {
        String text = "Value1: #{bean.value1}, Value2: ${bean.value2}, Value3: @{bean.value3}";

        // Convert both # and $ expressions
        String result1 = convertToFtl(text, new char[]{'#', '$'});
        System.out.println(result1);
        // Output: Value1: ${r"#{bean.value1}"}, Value2: ${r"${bean.value2}"}, Value3: @{bean.value3}

        // Convert only # expressions
        String result2 = convertToFtl(text, new char[]{'#'});
        System.out.println(result2);
        // Output: Value1: ${r"#{bean.value1}"}, Value2: ${bean.value2}, Value3: @{bean.value3}

        // Test idempotency
        String doubleConverted = convertToFtl(result1, new char[]{'#', '$'});
        System.out.println("Same as first conversion? " + result1.equals(doubleConverted));
        // Output: Same as first conversion? true
    }
}