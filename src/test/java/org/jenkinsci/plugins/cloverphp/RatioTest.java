package org.jenkinsci.plugins.cloverphp;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit test for {@link Ratio}
 */
public class RatioTest {

    final void assertRatio(Ratio r, float numerator, float denominator) {
        assertEquals(numerator, r.numerator);
        assertEquals(denominator, r.denominator);
    }

    /**
     * Tests that {@link Ratio#parseValue(String)} parses correctly float
     * numbers with either dot or comma as decimal point.
     *
     * @throws Exception
     */
    public void testParseValue() throws Exception {
        assertRatio(Ratio.create(1, 2), 1.0f, 2.0f);
    }

    /**
     * Test of toString method, of class Ratio.
     */
    @Test
    public void testToString() {
        Ratio ratio = Ratio.create(10.0f, 20.2f);
        assertEquals("10/20.2", ratio.toString());
    }

    /**
     * Test of getPercentage1d method, of class Ratio.
     */
    @Test
    public void testGetPercentage1d() {
        Ratio ratio = Ratio.create(1.0f, 3.0f);
        assertEquals("33.3", ratio.getPercentage1d());
    }

    /**
     * Test of getPercentageStr method, of class Ratio.
     */
    @Test
    public void testGetPercentageStr() {
        Ratio ratio = Ratio.create(1.0f, 3.0f);
        assertEquals("33.3%", ratio.getPercentageStr());
    }

    /**
     * Test of getPercentageStr method, of class Ratio.
     */
    @Test
    public void testGetPercentageStr_undefined() {
        Ratio ratio = Ratio.create(1.0f, 0.0f);
        assertEquals("-", ratio.getPercentageStr());
        ratio = Ratio.create(1.0f, -50.0f);
        assertEquals("-", ratio.getPercentageStr());
    }

    /**
     * Test of getPcWidth method, of class Ratio.
     */
    @Test
    public void testGetPcWidth_undefined() {
        Ratio ratio = Ratio.create(1.0f, -30.0f);
        assertEquals("-3.3%", ratio.getPcWidth());
    }

    /**
     * Test of getPcUncovered method, of class Ratio.
     */
    @Test
    public void testGetPcUncovered() {
        Ratio ratio = Ratio.create(1.0f, 30.0f);
        assertEquals("96.7%", ratio.getPcUncovered());
    }

    /**
     * Test of getPcCovered method, of class Ratio.
     */
    @Test
    public void testGetPcCovered_plus() {
        Ratio ratio = Ratio.create(1.0f, 30.0f);
        assertEquals("3.3%", ratio.getPcCovered());
    }

    /**
     * Test of getPcCovered method, of class Ratio.
     */
    @Test
    public void testGetPcCovered_undefined() {
        Ratio ratio = Ratio.create(1.0f, -30.0f);
        assertEquals("-", ratio.getPcCovered());
        ratio = Ratio.create(1.0f, 0.0f);
        assertEquals("-", ratio.getPcCovered());
    }

    /**
     * Test of getHasData method, of class Ratio.
     */
    @Test
    public void testGetHasData() {
        Ratio ratio = Ratio.create(1.0f, 30.0f);
        assertEquals("true", ratio.getHasData());
        ratio = Ratio.create(1.0f, -30.0f);
        assertEquals("false", ratio.getHasData());
    }

    /**
     * Test of getPercentage method, of class Ratio.
     */
    @Test
    public void testGetPercentage() {
        Ratio ratio = Ratio.create(1.0f, 30.0f);
        assertEquals(3, ratio.getPercentage());
        ratio = Ratio.create(1.0f, 40.0f);
        assertEquals(3, ratio.getPercentage());
    }

    /**
     * Test of getPercentageFloat method, of class Ratio.
     */
    @Test
    public void testGetPercentageFloat_0() {
        Ratio ratio = Ratio.create(1.0f, 0.0f);
        assertTrue(Float.compare(0.0f, ratio.getPercentageFloat()) == 0);
    }

    /**
     * Test of getPercentageFloat method, of class Ratio.
     */
    @Test
    public void testGetPercentageFloat_100() {
        Ratio ratio = Ratio.create(10.0f, 10.0f);
        assertTrue(Float.compare(100.0f, ratio.getPercentageFloat()) == 0);
    }

    /**
     * Test of getPercentageFloat method, of class Ratio.
     */
    @Test
    public void testGetPercentageFloat() {
        Ratio ratio = Ratio.create(5.0f, 10.0f);
        assertTrue(Float.compare(50.0f, ratio.getPercentageFloat()) == 0);
    }

    /**
     * Test of equals method, of class Ratio.
     */
    @Test
    public void testEquals_same() {
        Ratio ratio = Ratio.create(5.0f, 10.0f);
        assertTrue(ratio.equals(ratio));
    }

    /**
     * Test of equals method, of class Ratio.
     */
    @Test
    public void testEquals() {
        Ratio ratio = Ratio.create(5.0f, 10.0f);
        Ratio another = Ratio.create(5.0f, 10.0f);
        assertTrue(ratio.equals(another));
    }

    /**
     * Test of equals method, of class Ratio.
     */
    @Test
    public void testEquals_TargetNull() {
        Ratio ratio = Ratio.create(5.0f, 10.0f);
        assertFalse(ratio.equals(null));
        assertFalse(ratio.equals(Integer.class));
    }

    /**
     * Test of create method, of class Ratio.
     */
    @Test
    public void testCreate() {
        Ratio ratio = Ratio.create(5.0f, 10.0f);
        assertNotNull(ratio);
    }
}