package jit;

import java.io.File;

import org.junit.jupiter.api.Test;

import java.util.Optional;

class ClassLoaderTest
{
    @Test
    public void testClassLoader()
    {
        String root = "resources";
        String[] paths = new String[] { "demo/SimpleDemo.java" };
        ClassLoader classLoader = new ClassLoader(root, files);
        classLoader.loadClasses();
    }
}
