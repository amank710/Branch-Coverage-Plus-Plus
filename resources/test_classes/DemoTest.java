package demo;

import runtime.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Instrument(Demo.class)
@ExtendWith(InstrumentedTestExtension.class)
class DemoTest
{
    @Test
    public void testFoo()
    {
        Demo demo = new Demo();
        demo.foo(-1, 2);
    }

    @Test
    public void testBarReturns1()
    {
        Demo demo = new Demo();
        int ret = demo.bar(-1, -1);
        assertEquals(1, ret);
    }
}
