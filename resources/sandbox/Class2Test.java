import runtime.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

@ExtendWith(InstrumentedTestExtension.class)
@Instrument(Class2.class)
class Class2Test
{
    @Test
    void testFoo()
    {
        Class2 c = new Class2();
        c.foo(3);
    }

    @Test
    void testGoo() {
        Class2 c = new Class2();
        c.goo(6);
        c.goo(3);
    }
}
