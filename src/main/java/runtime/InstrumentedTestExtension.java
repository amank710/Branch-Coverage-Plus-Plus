package runtime;

import java.lang.reflect.Method;
import java.util.*;

import org.junit.jupiter.api.extension.*;

public class InstrumentedTestExtension implements BeforeAllCallback, BeforeEachCallback, TestInstanceFactory
{
    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        System.out.println("[InstrumentedTestExtension]: Finding instrumented types...");
        List<Class<?>> instClasses = getInstrumented(context.getRequiredTestClass());
        String[] classNames = Arrays.stream(instClasses.toArray()).map(Object::toString).toArray(String[]::new);
        System.out.println("[InstrumentedTestExtension]: Found instrumented types: " + Arrays.toString(classNames));
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        System.out.println("InstrumentedTestExtension: beforeEach");
    }

    @Override
    public Object createTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext context) throws TestInstantiationException
    {
        System.out.println("InstrumentedTestExtension: createTestInstance");

        try {
            return context.getRequiredTestClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new TestInstantiationException("Failed to create test instance", e);
        }
    }

    static <T> List<Class<?>> getInstrumented(Class<T> target)
    {
        Instrument annotation = target.getAnnotation(Instrument.class);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    "[InstrumentedTestExtension] Please annotate the test file with @Instrument");
        }
        if (annotation.value().length == 0) {
            throw new IllegalArgumentException(
                    "[InstrumentedTestExtension] Please specify at least one class to instrument");
        }

        return Arrays.asList(annotation.value());
    }

    static <T> List<String> getInstrumentable(Class<T> target)
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
