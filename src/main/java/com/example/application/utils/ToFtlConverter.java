package com.example.application.utils;

import java.util.regex.*;

public class ToFtlConverter {


    public static String convertToFtl(String input, char prefix) {
        if (input == null) {
            return "";
        }
        if (prefix != '#' && prefix != '$') {
            throw new IllegalArgumentException("Prefix must be either '#' or '$'");
        }
        
        // Regular expression to match #{...} or ${...} patterns
        String regex = Pattern.quote(String.valueOf(prefix)) + "\\{([^}]*)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        
        // Replace each match with the FreeMarker-escaped version
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String jsfExpr = matcher.group(0);  // The full #{...} or ${...} match
            String innerContent = matcher.group(1);  // The content inside {}
            
            // Escape the expression for FreeMarker
            matcher.appendReplacement(result, Matcher.quoteReplacement(
                "${r\"" + jsfExpr + "\"}"
            ));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }

    // Overloaded method with default '#' prefix for backward compatibility
    public static String convertToFtl(String input) {
        return convertToFtl(convertToFtl(input, '#'),'$');
    }

    // Example usage
    public static void main(String[] args) {
        String jsfText1 = "<h:outputText value=\"#{msg.hello}\" rendered=\"#{bean.condition}\"/>";
        String jsfText2 = "<h:outputText value=\"${msg.hello}\" rendered=\"${bean.condition}\"/>";
        
        System.out.println(convertToFtl(jsfText1, '#')); // With # prefix
        // Output: <h:outputText value="${r"#{msg.hello}"}" rendered="${r"#{bean.condition}"}"/>
        
        System.out.println(convertToFtl(jsfText2, '$')); // With $ prefix
        // Output: <h:outputText value="${r"${msg.hello}"}" rendered="${r"${bean.condition}"}"/>
    }
}