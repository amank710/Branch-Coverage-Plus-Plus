package runtime;

import java.io.PrintWriter;

import common.PathCoverage;

import java.util.Optional;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.listeners.LoggingListener;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;


import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;


public class TestExecutor
{
    private TestExecutionSummary summary;
    private Class clazz;

    private Optional<PathCoverage> pathCoverage = Optional.empty();

    public TestExecutor(String testClass) throws ClassNotFoundException
    {
        clazz = Class.forName(testClass);
    }

    public TestExecutor(Class clazz)
    {
        this.clazz = clazz;
    }

    public void runTests()
    {
        System.out.println("[TestExecutor] Running tests for " + clazz.getName());

        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(
                selectClass(clazz)
            )
            .build();

        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        LoggingListener loggingListener = LoggingListener.forJavaUtilLogging();
        TestExecutorListener testExecutorListener = new TestExecutorListener();

        try {
            Launcher launcher = LauncherFactory.create();

            launcher.registerTestExecutionListeners(listener);
            launcher.registerTestExecutionListeners(loggingListener);
            launcher.registerTestExecutionListeners(testExecutorListener);

            launcher.execute(request);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("[TestExecutor] Tests finished for " + clazz.getName());
        summary = listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        pathCoverage = testExecutorListener.getPathCoverage();
    }

    public PathCoverage getPathCoverage() throws PathCoverageNotFoundException
    {
        if (pathCoverage.isPresent())
        {
            return pathCoverage.get();
        }
        throw new PathCoverageNotFoundException("Path coverage not found");
    }

    public long getFoundTestCount()
    {
        return summary.getTestsFoundCount();
    }
}
