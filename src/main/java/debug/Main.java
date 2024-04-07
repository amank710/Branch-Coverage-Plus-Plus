package debug;

import common.PathCoverage;
import jit.RuntimeClassLoader;
import runtime.TestExecutor;

import java.util.Map;
import java.util.Optional;

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            String root = "/home/arunb/Projects/cpsc410/Group4Project2/resources/sandbox";
            String[] paths = new String[] { "DynamicClass.java", "DynamicClass3Test.java" };
            RuntimeClassLoader classLoader = new RuntimeClassLoader(root, paths);
            Map<String, Class<?>> classes = classLoader.loadClasses();
            TestExecutor testExecutor = new TestExecutor(classes.get("DynamicClass3Test"));
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
