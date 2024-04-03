package jit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

    public void loadClasses()
    {
        for (String localSource : files)
        {
            File file = new File(root + "/" + localSource);
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            compiler.run(null, null, null, file.getPath());

            try
            {
                System.out.println("[ClassLoader] URL: " + root.toURI().toURL());
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { root.toURI().toURL() });
                System.out.println("[ClassLoader] Loading class " + file.getName().replace(".java", ""));
                System.out.println("[ClassLoader] Loaded: " + classLoader.loadClass("demo.SimpleDemo" ));
            }
            catch (ClassNotFoundException|MalformedURLException e)
            {
                System.out.println("[ClassLoader] Error with loading: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
