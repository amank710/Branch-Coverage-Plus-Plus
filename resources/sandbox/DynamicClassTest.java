import runtime.Instrument;
import runtime.InstrumentedTestExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

@ExtendWith(InstrumentedTestExtension.class)
@Instrument(DynamicClass.class)
class DynamicClassTest
{
    @Test 
    public void testDynamicClass()
    {
        System.out.println("DynamicClass.testDynamicClass()");
        DynamicClass dynamicClass = new DynamicClass();
        dynamicClass.foo();
    }
}
