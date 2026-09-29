package io.jenkins.plugins.smarttests;

import org.junit.Test;
import static org.junit.Assert.*;

public class MathUtilsTest {

    @Test
    public void testAdd() {
        assertEquals(5, MathUtils.add(2, 3));
    }

    @Test
    public void testMultiply() {
        assertEquals(6, MathUtils.multiply(2, 3));
    }

    @Test
    public void testIsEven() {
        assertTrue(MathUtils.isEven(4));
        assertFalse(MathUtils.isEven(5));
    }
}
