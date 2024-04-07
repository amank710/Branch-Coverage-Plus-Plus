package demo1;

import runtime.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

@ExtendWith(InstrumentedTestExtension.class)
@Instrument(Class1.class)
class Class1Test
{
    @Test
    void testFoo()
    {
        Class1 c = new Class1();
        c.foo(3);
    }
}
