package runtime;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class InstrumentedTestExtension implements BeforeAllCallback, BeforeEachCallback
{
    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        System.out.println("InstrumentedTestExtension: beforeAll");
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        System.out.println("InstrumentedTestExtension: beforeEach");
    }
}
