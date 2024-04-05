package runtime;

import common.PathCoverage;
import common.util.Tuple;
import runtime.TestExecutor;
import jit.RuntimeClassLoader;
import jit.CompilationError;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;


class EndToEndPathCoverageTest
{
    @Test
    public void testEndToEndPathCoverage() throws ClassNotFoundException, CompilationError, PathCoverageNotFoundException
    {
        String root = Optional.ofNullable(System.getProperty("SANDBOX_HOME")).orElseThrow(() -> new IllegalArgumentException("SANDBOX_HOME not set"));
        String[] paths = new String[] { "DynamicClass.java", "DynamicClassTest.java" };
        RuntimeClassLoader classLoader = new RuntimeClassLoader(root, paths);
        Map<String, Class<?>> loadedClasses = classLoader.loadClasses();
        TestExecutor testExecutor = new TestExecutor(loadedClasses.get("DynamicClassTest"));
        testExecutor.runTests();
        PathCoverage pathCoverage = testExecutor.getPathCoverage();
        assertEquals(pathCoverage.getPathCoverageMetadata(), new Tuple<Integer, Integer>(1, 1));
    }
}
