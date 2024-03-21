package runtime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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

    public static <T> List<String> getInstrumentable(Class<T> target)
    {
        // Get methods of the class
        Method[] methods = target.getDeclaredMethods();

        // List of instrumentable method names
        List<String> instMethods = new ArrayList<String>();

        // Iterate and check for annotation
        for (Method method : methods) {
            // Annotation check
            if(method.isAnnotationPresent(Instrumentable.class)) {
                instMethods.add(method.getName());
            }
        }

        System.out.println(instMethods);

        return instMethods;
    }
}
