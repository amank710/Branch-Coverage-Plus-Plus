package runtime;

import common.functions.FunctionContext;
import common.functions.Path;
import common.PathCoverage;
import common.util.Tuple;
import parser.VariableMapBuilder;

import com.sun.jdi.AbsentInformationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
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
    Map<String, Set<List<Integer>>> satisfiablePaths;

    public InstrumentedTestExtension()
    {
        System.out.println("[InstrumentedTestExtension]: Initializing extension...");
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
        catch (Exception e)
        {
            System.out.println("[InstrumentedTestExtension] Error attaching CodeStepper to the target VM. Stack dump: ");
            e.printStackTrace();
            throw e;
        }

        Map<String, Map<String, Tuple<Integer, Integer>>> methodBounds = codeStepper.getMethodBounds();
        for (Class<?> instClass : instClasses)
        {
            URL location = instClass.getProtectionDomain().getCodeSource().getLocation();
            String path = location.getPath();
            if (System.getProperty("os.name").contains("Windows"))
            {
                path = path.substring(1);
            }
            System.out.println("[InstrumentedTestExtension]: Found source code at " + path);

            VariableMapBuilder variableMapBuilder = new VariableMapBuilder(path, instClass.getSimpleName() + ".java");
            variableMapBuilder.build();

            processStaticAnalysis(variableMapBuilder.getPath(), instClass.getName(), methodBounds);
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
    public void afterAll(ExtensionContext context) throws IOException
    {
        System.out.println("[InstrumentedTestExtension]: Test suite completed");
        System.out.println("[InstrumentedTestExtension]: Instrumented method paths: " + instrumentedMethodPaths);

        PathCoverage pathCoverage = calculatePathCoverage();
        System.out.println("[InstrumentedTestExtension]: Path coverage: " + pathCoverage);

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(pathCoverage);
            oos.flush();
            oos.close();
            
            context.publishReportEntry("coverage", baos.toString("ISO-8859-1"));
        }
        catch (IOException e)
        {
            System.out.println("[InstrumentedTestExtension]: Error serializing path coverage. Stack dump: ");
            e.printStackTrace();
            throw e;
        }
    }

    Map<Integer, Integer> getLineHits(String methodName)
    {
        Set<Path> paths = instrumentedMethodPaths.get(methodName);
        Map<Integer, Integer> lineHits = new HashMap<>();

        for (Path path : paths)
        {
            for (Integer line : path)
            {
                lineHits.put(line, lineHits.getOrDefault(line, 0) + 1);
            }
        }

        return lineHits;
    }

    Set<List<Integer>> getUncoveredPathSegments(String methodName)
    {
        HashSet<List<Integer>> uncoveredPaths = new HashSet<>();

        for (List<Integer> pathSegment : satisfiablePaths.get(methodName))
        {
            if (!isCovered(methodName, pathSegment))
            {
                uncoveredPaths.add(pathSegment);
            }

        }

        return uncoveredPaths;
    }

    void setExploredPaths(String methodName, Set<Path> paths)
    {
        instrumentedMethodPaths.put(methodName, paths);
    }

    Set<List<Integer>> getSatisfiablePaths(String methodName)
    {
        return satisfiablePaths.get(methodName);
    }

    void setSatisfiablePaths(String methodName, Set<List<Integer>> viablePaths)
    {
        satisfiablePaths.put(methodName, viablePaths);
        System.out.println("[InstrumentedTestExtension]: Satisfiable paths for " + methodName + ": " + viablePaths);
    }

    Set<List<Integer>> getExplorablePaths(String methodName)
    {
        return satisfiablePaths.get(methodName);
    }

    PathCoverage calculatePathCoverage()
    {
        try
        {
            Map<String, Tuple<Integer, Integer>> pathCoverageMetadata = new HashMap<>();
            Map<String, Map<Integer, Integer>> lineHits = new HashMap<>();
            Map<String, Set<List<Integer>>> uncoveredPaths = new HashMap<>();

            for (String methodName : satisfiablePaths.keySet())
            {
                System.out.println("[InstrumentedTestExtension]: Calculating path coverage for " + methodName);
                Map<Integer, Integer> methodlineHits = getLineHits(methodName);
                Set<List<Integer>> methodUncoveredPaths = getUncoveredPaths(methodName);
                int totalPaths = getTotalPaths(methodName);
                int numCoveredPaths = totalPaths - methodUncoveredPaths.size();

                pathCoverageMetadata.put(methodName, new Tuple<>(numCoveredPaths, totalPaths));
                lineHits.put(methodName, methodlineHits);
                uncoveredPaths.put(methodName, methodUncoveredPaths);
            }

            return new PathCoverage(pathCoverageMetadata, lineHits, uncoveredPaths);
        }
        catch (Exception e)
        {
            System.out.println("[InstrumentedTestExtension]: Error calculating path coverage. Stack dump: ");
            e.printStackTrace();
            throw e;
        }
    }

    void processStaticAnalysis(Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> > path, String className, Map<String, Map<String, Tuple<Integer, Integer>>> classToMethodBounds)
    {
        Map<String, Tuple<Integer, Integer>> methodBounds = classToMethodBounds.get(className);
        Set<List<Integer>> localPathRep = convertToLocalPathRep(path);
        System.out.println("[InstrumentedTestExtension]: Local path representation for " + className + ": " + localPathRep);

        Set<List<Integer>> filteredPaths = filterPath(methodBounds, localPathRep);

        System.out.println("[InstrumentedTestExtension]: Filtered path for " + className + ": " + filteredPaths);
        for (List<Integer> filteredPath : filteredPaths)
        {
            List<Integer> pathCopy = filteredPath;
            Collections.sort(pathCopy);

            for (Map.Entry<String, Tuple<Integer, Integer>> methodBoundSet : methodBounds.entrySet())
            {
                if (pathCopy.get(0) >= methodBoundSet.getValue().first() && pathCopy.get(pathCopy.size() - 1) <= methodBoundSet.getValue().second())
                {
                    Set<List<Integer>> methodPaths = satisfiablePaths.getOrDefault(methodBoundSet.getKey(), new HashSet<>());
                    methodPaths.add(filteredPath);
                    satisfiablePaths.put(methodBoundSet.getKey(), methodPaths);
                }
            }
        }
    }

    private int getTotalPaths(String methodName)
    {
        return satisfiablePaths.get(methodName).size();
    }

    private Set<List<Integer>> getUncoveredPaths(String methodName)
    {
        Set<List<Integer>> uncoveredPathSegments = getUncoveredPathSegments(methodName);

        return uncoveredPathSegments;
    }

    private Boolean isCovered(String methodName, List<Integer> segment)
    {
        Collections.sort(segment);

        for (Path executedPath : instrumentedMethodPaths.get(methodName))
        {
            LinkedList<Integer> segmentQueue = new LinkedList<>(segment);

            for (Integer lineNumber : executedPath)
            {
                Integer segmentLineNumber = segmentQueue.peek();

                if (lineNumber.equals(segmentLineNumber))
                {
                    segmentQueue.pop();
                }

                if (segmentQueue.isEmpty())
                {
                    return true;
                }
            }
        }

        return false;
    }

    private Set<List<Integer>> convertToLocalPathRep(Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> path)
    {
        Set<List<Integer>> localPathRep = new HashSet<>();

        for (Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> pathSegment : path)
        {
            for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> entry : pathSegment.entrySet())
            {
                localPathRep.addAll(entry.getValue());
            }
        }

        return localPathRep;
    }

    private Set<List<Integer>> filterPath(Map<String, Tuple<Integer, Integer>> methodBounds, Set<List<Integer>> paths)
    {
        Set<List<Integer>> filteredPath = new HashSet<>();

        for (List<Integer> path : paths)
        {
            int start = path.get(0);
            boolean start_bounded = false;
            int end = path.get(path.size() - 1);
            boolean end_bounded = false;

            for (Tuple<Integer, Integer> bounds : methodBounds.values())
            {
                if (start >= bounds.first() && start <= bounds.second())
                {
                    start_bounded = true;
                }
                if (end >= bounds.first() && end <= bounds.second())
                {
                    end_bounded = true;
                }

                if (start_bounded && end_bounded)
                {
                    filteredPath.add(path);
                    break;
                }
            }
        }

        return filteredPath;
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
