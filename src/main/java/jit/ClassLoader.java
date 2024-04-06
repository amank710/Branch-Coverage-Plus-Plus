package jit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.tools.ToolProvider;
import javax.tools.JavaCompiler;

public class ClassLoader
{
    private String[] files;
    private String root;

    public ClassLoader(String root, String[] files)
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
            System.out.println("[ClassLoader] Compilation failed");
            throw new CompilationError("Compilation of " + args + " failed");
        }

        Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
        for (String localSource : files)
        {
            try
            {
                File fileRoot = new File(root);
                System.out.println("[ClassLoader] URL: " + fileRoot);
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { fileRoot.toURI().toURL() });
                String className = localSource.replace(".java", "");
                System.out.println("[ClassLoader] Loading class " + className);
                classes.put(className, classLoader.loadClass(className));
            }
            catch (ClassNotFoundException|MalformedURLException e)
            {
                System.out.println("[ClassLoader] Error with loading: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return classes;
    }
}
