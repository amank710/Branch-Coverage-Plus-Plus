import org.junit.jupiter.api.extension.BeforeEachCallback;

public class InstrumentedTestExtension extends BeforeEachCallback
{
    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        System.out.println("Before each test method: " + context.getTestMethod().get().getName());
    }
}
