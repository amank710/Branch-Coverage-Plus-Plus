package demo;

import runtime.Instrumentable;
import runtime.InstrumentedTestExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
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

    public <T> List<String> getInstrumentable(Class<T> target)
    {
        // Get methods of the class
        Method[] methods = target.getDeclaredMethods();

        // List of instrumentable method names
        List<String> instMethods = new ArrayList<String>();

        // Iterate and check for annotation
        for (Method method : methods) {
            // Annotation check
            if(method.isAnnotationPresent(Instrumentable.class)) {
                instMethods.add(method.getName());
            }
        }

        System.out.println(instMethods);

        return instMethods;
    }

    @Test
    public void testInstrumentable()
    {
        Class<SimpleDemo> targetClass = SimpleDemo.class;

        List<String> instMethods = getInstrumentable(targetClass);

        assertFalse(instMethods.contains("abs"));
        assertTrue(instMethods.contains("test"));
        assertTrue(instMethods.contains("foo"));
    }
}
