package demo;

import runtime.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Instrument(SimpleDemo.class)
@ExtendWith(InstrumentedTestExtension.class)
public class SimpleDemoTest
{
    @Test
    public void testAbs()
    {
        SimpleDemo demo = new SimpleDemo();
        assertEquals(5, demo.abs(5));
    }

    @Test
    public void testAbsNeg()
    {
        SimpleDemo demo = new SimpleDemo();
        assertEquals(5, demo.abs(-5));
    }

    @Test
    public void testSameSign()
    {
        SimpleDemo demo = new SimpleDemo();
        assertTrue(demo.isSameSign(5, 10));
    }
}
