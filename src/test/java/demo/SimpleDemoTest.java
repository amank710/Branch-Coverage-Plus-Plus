package demo;

import runtime.Instrumentable;
import runtime.InstrumentedTestExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(InstrumentedTestExtension.class)
public class SimpleDemoTest
{
    @Test
    public void testAbs()
    {
        SimpleDemo demo = new SimpleDemo();
        assertEquals(5, demo.abs(5));
    }
}
