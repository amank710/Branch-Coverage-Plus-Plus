package runtime;

import demo.SimpleDemo;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InstrumentedTestExtensionTest
{
    class InstrumentableDemo
    {
        @Instrumentable
        public void instrumentable() {}

        public void notInstrumentable() {}
    }

    @Instrument(value = {InstrumentableDemo.class})
    class InstrumentedDemo {}

    @Test
    public void testInstrumentable()
    {
        Class<SimpleDemo> targetClass = SimpleDemo.class;

        List<String> instMethods = InstrumentedTestExtension.getInstrumentable(targetClass);

        assertFalse(instMethods.contains("abs"));
        assertTrue(instMethods.contains("test"));
        assertTrue(instMethods.contains("foo"));
    }

    @Test
    public void testInstrumented()
    {
        Class<InstrumentedDemo> targetClass = InstrumentedDemo.class;

        List<Class<?>> instClasses = InstrumentedTestExtension.getInstrumented(targetClass);

        assertEquals(1, instClasses.size());
        assertEquals(InstrumentableDemo.class, instClasses.get(0));
    }
}
