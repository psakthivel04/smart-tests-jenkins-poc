package io.jenkins.plugins.smarttests;

import org.junit.Test;
import static org.junit.Assert.*;

public class DemoTest {

    @Test
    public void testAddition() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testStringUpper() {
        assertEquals("HELLO", "hello".toUpperCase());
    }

    @Test
    public void testListSize() {
        java.util.List<String> items = java.util.Arrays.asList("a", "b", "c");
        assertEquals(3, items.size());
    }
}
