package jit;

import runtime.TestExecutor;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

class ClassLoaderTest
{
    @Test
    public void testClassLoader() throws ClassNotFoundException
    {
        String root = Optional.ofNullable(System.getProperty("SANDBOX_HOME")).orElseThrow(() -> new IllegalArgumentException("SANDBOX_HOME not set"));
        String[] paths = new String[] { "DynamicClass.java", "DynamicClassTest.java" };
        ClassLoader classLoader = new ClassLoader(root, paths);
        Map<String, Class<?>> classes = classLoader.loadClasses();
        TestExecutor testExecutor = new TestExecutor(classes.get("DynamicClassTest"));
        testExecutor.runTests();
    }
}
