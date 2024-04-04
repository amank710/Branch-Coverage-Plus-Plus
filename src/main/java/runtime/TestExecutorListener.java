package runtime;

import common.PathCoverage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Optional;
import org.junit.platform.engine.reporting.ReportEntry;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;


class TestExecutorListener implements TestExecutionListener
{
    private Optional<PathCoverage> pathCoverage = Optional.empty();

    public void reportingEntryPublished(TestIdentifier testIdentifier, ReportEntry entry)
    {
        System.out.println("[TestExecutorListener] Got a published reporting entry");
        Map<String, String> keyValPairs = entry.getKeyValuePairs();
        if (keyValPairs != null && keyValPairs.containsKey("coverage"))
        {
            System.out.println("[TestExecutorListener] Found coverage data: " + keyValPairs.get("coverage"));

            // deserialize the coverage data
            try
            {
                ByteArrayInputStream bis = new ByteArrayInputStream(keyValPairs.get("coverage").getBytes("ISO-8859-1"));
                ObjectInputStream ois = new ObjectInputStream(bis);
                PathCoverage coverage = (PathCoverage) ois.readObject();
                ois.close();

                pathCoverage = Optional.of(coverage);
                System.out.println("[TestExecutorListener] Deserialized coverage data: " + coverage);
            }
            catch (IOException|ClassNotFoundException e)
            {
                System.out.println("[TestExecutorListener] Error deserializing coverage data. Failed to deserialize " + e.getMessage());
            }
        }
    }

    public Optional<PathCoverage> getPathCoverage()
    {
        return pathCoverage;
    }
}
