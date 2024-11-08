package org.lebastudios.theroundtable.communications;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest
{

    Version version1 = new Version(1, 0, 0, "RELEASE");
    Version version2 = new Version("1.0.0-RELEASE");
    Version version3 = new Version(1, 0, 0, "SNAPSHOT");
    Version version4 = new Version("1.0.0-SNAPSHOT");
    Version version5 = new Version(11, 2, 0, "ALPHA");
    Version version6 = new Version("2.0.9");
    Version version7 = new Version("2.0.23");
    Version version8 = new Version("2.0.23-RC");
    Version version9 = new Version("1.0.0");
    
    @Test
    void isGreaterThan()
    {
        assertFalse(version2.isGreaterThan(version1));
        assertFalse(version3.isGreaterThan(version1));
        assertFalse(version4.isGreaterThan(version1));
        assertTrue(version5.isGreaterThan(version1));
        assertTrue(version6.isGreaterThan(version1));
        assertTrue(version7.isGreaterThan(version6));
        assertFalse(version8.isGreaterThan(version7));
        
        assertThrowsExactly(IllegalArgumentException.class, () -> new Version("1.0.0-"));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Version("1.30-"));
        assertThrowsExactly(IllegalArgumentException.class, () -> new Version("1.3.a0-"));
    }

    @Test
    void isLessThan()
    {
        assertFalse(version5.isLessThan(version1));
        assertFalse(version1.isLessThan(version2));
        assertFalse(version1.isLessThan(version3));
        assertFalse(version1.isLessThan(version4));
        assertTrue(version1.isLessThan(version5));
        assertTrue(version1.isLessThan(version6));
        assertTrue(version6.isLessThan(version7));
        assertFalse(version7.isLessThan(version8));
    }

    @Test
    void isEqualTo()
    {
        assertTrue(version1.isEqualTo(version2));
        assertFalse(version1.isEqualTo(version3));
        assertFalse(version1.isEqualTo(version4));
        assertTrue(version1.isEqualTo(version9));
    }
}