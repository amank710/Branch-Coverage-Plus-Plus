package demo;

import runtime.Instrument;
import runtime.InstrumentedTestExtension;
import source.SourceCodeBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Test;

@ExtendWith(InstrumentedTestExtension.class)
@Instrument(SourceCodeBoolean.class)
class SourceCodeBooleanTest
{
    @Test
    public void testBooleanFlagsTrue()
    {
        assertTrue(SourceCodeBoolean.booleanFlags(true));
    }
}
