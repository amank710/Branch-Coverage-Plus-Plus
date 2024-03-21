import demo.SimpleDemo;
import runtime.InstrumentedTestExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

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
