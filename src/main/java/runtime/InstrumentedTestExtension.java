package runtime;

import common.functions.FunctionContext;
import common.functions.Path;
import common.util.Tuple;
import graph.Node;
import parser.VariableMapBuilder;

import com.sun.jdi.AbsentInformationException;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.*;

public class InstrumentedTestExtension implements AfterAllCallback, AfterEachCallback, BeforeAllCallback, BeforeEachCallback
{
    CodeStepper codeStepper;

    // actual lines executed during runtime
    Map<String, Set<Path>> instrumentedMethodPaths;

    // input to the dynamic analysis
    Map<String, FunctionContext> instrumentedMethodContext;
    Map<String, List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>>> satisfiablePaths;

    public InstrumentedTestExtension()
    {
        instrumentedMethodPaths = new HashMap<>();
        satisfiablePaths = new HashMap<>();
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception
    {
        System.out.println("[InstrumentedTestExtension]: Finding instrumented types...");

        List<Class<?>> instClasses = getInstrumented(context.getRequiredTestClass());
        String[] classNames = Arrays.stream(instClasses.toArray()).map(Object::toString).toArray(String[]::new);
        System.out.println("[InstrumentedTestExtension]: Found instrumented types: " + Arrays.toString(classNames));

        Map<String, Set<String>> instrumentedMethodMapping = new HashMap<>();
        for (Class<?> instClass : instClasses) {
            String path_home = Optional.ofNullable(System.getProperty("PATH_COVERAGE_SOURCE_HOME")).orElseThrow(() -> new IllegalArgumentException("Please set the PATH_COVERAGE_SOURCE_HOME environment variable"));
            String local_source_path = instClass.getPackage().getName().replaceAll("\\.", "/");
            System.out.println("[InstrumentedTestExtension]: Trying to find source code at " + path_home + "/" + local_source_path + "/" + instClass.getSimpleName() + ".java");

            VariableMapBuilder variableMapBuilder = new VariableMapBuilder(path_home + "/" + local_source_path, instClass.getSimpleName() + ".java");
            Node root = variableMapBuilder.build();

            Set<Method> instMethods = getInstrumentable(instClass);
            System.out.println("[InstrumentedTestExtension]: Found instrumentable methods for " + instClass.getName() + ": " + instMethods);
            instrumentedMethodMapping.put(instClass.getName(), instMethods.stream().map(Method::getName).collect(Collectors.toSet()));
        }

        codeStepper = new CodeStepper(instrumentedMethodMapping);
        try
        {
            codeStepper.run();
            System.out.println("CodeStepper attached to the target VM successfully!");
        }
        catch (AbsentInformationException e)
        {
            System.out.println("[InstrumentedTestExtension] Please run the test with debug information enabled");
            System.exit(1);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception
    {
        codeStepper.reset();
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception
    {
        System.out.println("[InstrumentedTestExtension]: Test: " + context.getDisplayName() + " completed");
        Map<String, List<Tuple<Integer, Long>>> exploredTestPaths = codeStepper.getExploredPaths(); 
        for (Map.Entry<String, List<Tuple<Integer, Long>>> entry : exploredTestPaths.entrySet())
        {
            String methodName = entry.getKey();
            Set<Path> paths = instrumentedMethodPaths.getOrDefault(methodName, new HashSet<>());
            Path path = new Path();
            entry.getValue().forEach(t -> path.addLine(t.first()));
            paths.add(path);
            setExploredPaths(methodName, paths);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception
    {
        System.out.println("[InstrumentedTestExtension]: Test suite completed");
        System.out.println("[InstrumentedTestExtension]: Instrumented method paths: " + instrumentedMethodPaths);
        printCoverage();
    }

    public Map<String, Set<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>>> getUncoveredPathSegments()
    {
        Map<String, Set<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>>> uncoveredPathSegments = new HashMap<>();

        for (Map.Entry<String, List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>>> entry : satisfiablePaths.entrySet())
        {
            for (Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>> path_segment : entry.getValue())
            {
                for (ArrayList<Integer> segment : path_segment.second())
                {
                    if (!isCovered(entry.getKey(), segment))
                    {
                        uncoveredPathSegments.putIfAbsent(entry.getKey(), new HashSet<>());
                        uncoveredPathSegments.get(entry.getKey()).add(path_segment);
                    
                    }
                }
            }
            //List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>> satisfiablePaths = entry.getValue();
            //Set<Path> exploredPaths = instrumentedMethodPaths.get(methodName);
            //Set<Path> uncoveredPaths = getUncoveredPaths(satisfiablePaths, exploredPaths);
            //uncoveredPathSegments.put(methodName, uncoveredPaths.stream().map(Path::getSegments).collect(Collectors.toSet()));
        }

        return uncoveredPaths;
    }

    void setExploredPaths(String methodName, Set<Path> paths)
    {
        instrumentedMethodPaths.put(methodName, paths);
    }

    void setSatisfiablePaths(String methodName, Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> viablePaths)
    {
        List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>> paths = new ArrayList<>();

        for (Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> path : viablePaths)
        {
            for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> entry : path.entrySet())
            {
                Tuple<Integer, Integer> range = new Tuple<>(entry.getKey().get(0), entry.getKey().get(1));
                paths.add(new Tuple<>(range, entry.getValue()));
            }
        }

        satisfiablePaths.put(methodName, paths);
    }

    List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>> getExplorablePaths(String methodName)
    {
        return satisfiablePaths.get(methodName);
    }

    private void printCoverage()
    {
        System.out.println("[InstrumentedTestExtension]: Printing coverage...");
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
