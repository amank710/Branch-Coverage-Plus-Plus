package demo;

import runtime.Instrument;
import runtime.InstrumentedTestExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

//@Instrument(SimpleDemo.class)
//@ExtendWith(InstrumentedTestExtension.class)
public class SimpleDemo2Test
{
    @Test
    void test1()
    {
        System.out.println("test1");
        SimpleDemo demo = new SimpleDemo();
        demo.abs(5);
    }
}
