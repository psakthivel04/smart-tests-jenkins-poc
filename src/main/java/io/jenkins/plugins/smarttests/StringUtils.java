package io.jenkins.plugins.smarttests;

public class StringUtils {

    public static String reverse(String s) {
        return new StringBuilder(s).reverse().toString();
    }

    public static boolean isPalindrome(String s) {
        String cleaned = s.toLowerCase();
        return cleaned.equals(reverse(cleaned));
    }

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
