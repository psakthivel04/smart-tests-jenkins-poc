package io.jenkins.plugins.smarttests;

import org.junit.Test;
import static org.junit.Assert.*;

public class StringUtilsTest {

    @Test
    public void testReverse() {
        assertEquals("cba", StringUtils.reverse("abc"));
    }

    @Test
    public void testIsPalindrome() {
        assertTrue(StringUtils.isPalindrome("racecar"));
        assertFalse(StringUtils.isPalindrome("hello"));
    }

    @Test
    public void testCapitalize() {
        assertEquals("Hello", StringUtils.capitalize("hello"));
    }
}
