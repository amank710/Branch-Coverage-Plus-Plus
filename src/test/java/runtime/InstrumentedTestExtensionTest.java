package runtime;

import demo.SimpleDemo;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstrumentedTestExtensionTest
{
    @Test
    public void testInstrumentable()
    {
        Class<SimpleDemo> targetClass = SimpleDemo.class;

        List<String> instMethods = InstrumentedTestExtension.getInstrumentable(targetClass);

        assertFalse(instMethods.contains("abs"));
        assertTrue(instMethods.contains("test"));
        assertTrue(instMethods.contains("foo"));
    }
}
