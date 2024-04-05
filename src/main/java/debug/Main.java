package debug;

import common.PathCoverage;
import jit.ClassLoader;
import jit.CompilationError;
import runtime.PathCoverageNotFoundException;
import runtime.TestExecutor;

import java.util.Map;
import java.util.Optional;

class Main
{
    public static void main(String[] args)
    {
        try
        {
            String root = Optional.ofNullable(System.getProperty("SANDBOX_HOME")).orElseThrow(() -> new IllegalArgumentException("SANDBOX_HOME not set"));
            String[] paths = new String[] { "DynamicClass.java", "DynamicClassTest.java" };
            ClassLoader classLoader = new ClassLoader(root, paths);
            Map<String, Class<?>> classes = classLoader.loadClasses();
            TestExecutor testExecutor = new TestExecutor(classes.get("DynamicClassTest"));
            testExecutor.runTests();
            PathCoverage pathCoverage = testExecutor.getPathCoverage();
            System.out.println(pathCoverage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
