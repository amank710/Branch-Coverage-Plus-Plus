package runtime;

import demo.SimpleDemoTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestExecutorTest
{
    @Test
    public void testTestExecutor() throws ClassNotFoundException
    {
        TestExecutor executor = new TestExecutor(SimpleDemoTest.class.getName());
        executor.runTests();
        assertEquals(3, executor.getFoundTestCount());
    }

}
