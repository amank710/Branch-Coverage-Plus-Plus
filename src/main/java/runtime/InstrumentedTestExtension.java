package runtime;

import com.sun.tools.attach.VirtualMachine;
import java.lang.reflect.Method;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.*;

public class InstrumentedTestExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback
{
    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        System.out.println("[InstrumentedTestExtension]: Finding instrumented types...");

        List vms = VirtualMachine.list();
        System.out.println("[InstrumentedTestExtension]: Found VMs: " + vms.toString());

        List<Class<?>> instClasses = getInstrumented(context.getRequiredTestClass());
        String[] classNames = Arrays.stream(instClasses.toArray()).map(Object::toString).toArray(String[]::new);
        System.out.println("[InstrumentedTestExtension]: Found instrumented types: " + Arrays.toString(classNames));

        Map<String, Set<String>> instrumentedMethodMapping = new HashMap<>();
        for (Class<?> instClass : instClasses) {
            Set<Method> instMethods = getInstrumentable(instClass);
            System.out.println("[InstrumentedTestExtension]: Found instrumentable methods for " + instClass.getName() + ": " + instMethods);
            instrumentedMethodMapping.put(instClass.getName(), instMethods.stream().map(Method::getName).collect(Collectors.toSet()));
        }

        CodeStepper codeStepper = new CodeStepper(instrumentedMethodMapping);
        try
        {
            codeStepper.run();
            System.out.println("Code stepper executed successfully");
        }
        catch (Exception e)
        {
            System.out.println("Failed to execute code stepper");
            e.printStackTrace();
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        System.out.println("InstrumentedTestExtension: beforeEach. Test instance: " + context.getRequiredTestInstance().toString());
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        System.out.println("InstrumentedTestExtension: afterEach. Test instance: " + context.getRequiredTestInstance().toString());
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

    static <T> Set<Method> getInstrumentable(Class<T> target)
    {
        // Get methods of the class
        Method[] methods = target.getDeclaredMethods();

        // List of instrumentable method names
        Set<Method> instMethods = new HashSet<Method>();

        // Iterate and check for annotation
        for (Method method : methods) {
            // Annotation check
            if(method.isAnnotationPresent(Instrumentable.class)) {
                instMethods.add(method);
            }
        }

        return instMethods;
    }
}
