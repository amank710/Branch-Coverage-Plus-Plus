package jit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import runtime.Instrumentable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;

public class RuntimeClassLoader
{
    private String[] files;
    private String root;

    public RuntimeClassLoader(String root, String[] files)
    {
        this.root = root;
        this.files = files;
    }

    public Map<String, Class<?>> loadClasses() throws CompilationError
    {
        ArrayList<String> args = new ArrayList<String>();
        args.add("-g");
        for (String file : files)
        {
            args.add(root + "/" + file);
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler.run(null, null, null, args.toArray(new String[0])) != 0)
        {
            System.out.println("[RuntimeClassLoader] Compilation failed");
            throw new CompilationError("Compilation of " + args + " failed");
        }

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

        File fileRoot = new File(root);
        System.out.println("[RuntimeClassLoader] URL: " + fileRoot);

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader runtimeClassLoader = null;
        try
        {
            runtimeClassLoader = new URLClassLoader(new URL[]{fileRoot.toURI().toURL()}, classLoader);
            Thread.currentThread().setContextClassLoader(runtimeClassLoader);
        }
        catch (Exception e)
        {
            System.out.println("[RuntimeClassLoader] Error with URL: " + e.getMessage());
            e.printStackTrace();
        }

        for (String localSource : files)
        {
            try
            {
                String className = localSource.replace(".java", "");
                System.out.println("[ClassLoader] Loading class " + className);
                classes.put(className, runtimeClassLoader.loadClass(className));
            }
            catch (ClassNotFoundException e)
            {
                System.out.println("[ClassLoader] Error with loading: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return classes;
    }
}
