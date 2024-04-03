package demo;

import runtime.Instrument;
import runtime.InstrumentedTestExtension;
import source.SourceCode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;


@Instrument(SourceCode.class)
@ExtendWith(InstrumentedTestExtension.class)
public class SourceCodeTest {
    @Test
    public void testFoo() {
        SourceCode sourceCode = new SourceCode();
        assertEquals("done", sourceCode.foo(0));
    }
}
