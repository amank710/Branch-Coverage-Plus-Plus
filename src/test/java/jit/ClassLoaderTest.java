package jit;

import runtime.TestExecutor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Map;
import java.util.Optional;

class ClassLoaderTest
{
    @Test
    public void testClassLoader() throws ClassNotFoundException, CompilationError
    {
        String root = Optional.ofNullable(System.getProperty("SANDBOX_HOME")).orElseThrow(() -> new IllegalArgumentException("SANDBOX_HOME not set"));
        String[] paths = new String[] { "DynamicClass.java", "DynamicClassTest.java" };
        RuntimeClassLoader classLoader = new RuntimeClassLoader(root, paths);
        assertDoesNotThrow(() -> classLoader.loadClasses());
    }

    @Test
    public void testClassLoadWithTestExecutor() throws ClassNotFoundException, CompilationError
    {
        String root = Optional.ofNullable(System.getProperty("SANDBOX_HOME")).orElseThrow(() -> new IllegalArgumentException("SANDBOX_HOME not set"));
        String[] paths = new String[] { "DynamicClass.java", "DynamicClassTest.java" };
        RuntimeClassLoader classLoader = new RuntimeClassLoader(root, paths);
        Map<String, Class<?>> classes = classLoader.loadClasses();
        TestExecutor testExecutor = new TestExecutor(classes.get("DynamicClassTest"));
        assertDoesNotThrow(() -> testExecutor.runTests());
    }
}
