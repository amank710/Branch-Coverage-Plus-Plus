package runtime;

import demo.SimpleDemo;

import org.junit.jupiter.api.Test;

import common.functions.Path;
import common.util.Tuple;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class InstrumentedTestExtensionTest
{
    class InstrumentableDemo
    {
        @Instrumentable
        public void instrumentable() {}

        public void notInstrumentable() {}
    }

    @Instrument(value = {InstrumentableDemo.class})
    class InstrumentedDemo {}

    @Test
    public void testInstrumentable()
    {
        Class<SimpleDemo> targetClass = SimpleDemo.class;

        List<String> instMethods = InstrumentedTestExtension.getInstrumentable(targetClass).stream().map(m -> m.getName()).toList();

        assertFalse(instMethods.contains("foo"));
        assertTrue(instMethods.contains("isSameSign"));
        assertTrue(instMethods.contains("abs"));
    }

    @Test
    public void testInstrumented()
    {
        Class<InstrumentedDemo> targetClass = InstrumentedDemo.class;

        List<Class<?>> instClasses = InstrumentedTestExtension.getInstrumented(targetClass);

        assertEquals(1, instClasses.size());
        assertEquals(InstrumentableDemo.class, instClasses.get(0));
    }

    @Test
    public void testCoveredPathsAllCovered()
    {
        InstrumentedTestExtension extension = new InstrumentedTestExtension();
        extension.setSatisfiablePaths("testFunc", createMockPaths());
        List<Tuple<Tuple<Integer, Integer>, ArrayList<ArrayList<Integer>>>> paths = extension.satisfiablePaths.get("testFunc");

        assertNotNull(paths);
        System.out.println(paths);

        extension.setExploredPaths("testFunc", createMockTestPaths());
    }

    private Set<Path> createMockTestPaths()
    {
        Set<Path> paths = new HashSet<>();

        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 20, 21, 23, 25, 26, 27, 47, 48, 64, 65, 66, 69, 71, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 20, 21, 45, 47, 48, 64, 72, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 49, 50, 53, 54, 55, 56, 64, 65, 66, 69, 71, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 49, 50, 53, 54, 55, 56, 64, 72, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 49, 50, 53, 54, 57, 64, 72, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 20, 21, 23, 25, 28, 30, 47, 48, 64, 65, 66, 69, 71, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 20, 21, 23, 31, 32, 33, 34, 37, 38, 42, 44, 47, 48, 64, 65, 66, 69, 71, 160
        }))));
        paths.add(new Path(new TreeSet<Integer>(Arrays.asList(new Integer[]{
            14, 15, 17, 19, 49, 50, 51, 52, 64, 72, 160
        }))));

        return paths;
    }

    private Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> createMockPaths()
    {
        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> if_block_1 = new HashMap<>();
        ArrayList<Integer> if_block_1_range = new ArrayList<Integer>(Arrays.asList(new Integer[]{19, 48}));
        ArrayList<Integer> if_block_1_path_1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{20, 21, 47, 23, 25, 26}));
        ArrayList<Integer> if_block_1_path_2 = new ArrayList<Integer>(Arrays.asList(new Integer[]{20, 21, 47, 23, 25, 28}));
        ArrayList<Integer> if_block_1_path_3 = new ArrayList<Integer>(Arrays.asList(new Integer[]{20, 21, 47, 23, 31, 42, 32, 37, 33}));
        ArrayList<Integer> if_block_1_path_4 = new ArrayList<Integer>(Arrays.asList(new Integer[]{20, 21, 47, 23, 31, 42, 39}));
        ArrayList<Integer> if_block_1_path_5 = new ArrayList<Integer>(Arrays.asList(new Integer[]{20, 21, 47, 45}));
        if_block_1.put(if_block_1_range,
                new ArrayList<ArrayList<Integer>>(Arrays.asList(
                        if_block_1_path_1,
                        if_block_1_path_2,
                        if_block_1_path_3,
                        if_block_1_path_4,
                        if_block_1_path_5)));

        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> else_block_1 = new HashMap<>();
        ArrayList<Integer> else_block_1_range = new ArrayList<Integer>(Arrays.asList(new Integer[]{48, 60}));
        ArrayList<Integer> else_block_1_path_1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{49, 50, 51}));
        ArrayList<Integer> else_block_1_path_2 = new ArrayList<Integer>(Arrays.asList(new Integer[]{49, 50, 53, 54, 55}));
        ArrayList<Integer> else_block_1_path_3 = new ArrayList<Integer>(Arrays.asList(new Integer[]{49, 50, 53, 54, 57}));
        else_block_1.put(else_block_1_range,
                new ArrayList<ArrayList<Integer>>(Arrays.asList(
                        else_block_1_path_1,
                        else_block_1_path_2,
                        else_block_1_path_3)));

        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> if_block_2 = new HashMap<>();
        ArrayList<Integer> if_block_2_range = new ArrayList<Integer>(Arrays.asList(new Integer[]{64, 71}));
        ArrayList<Integer> if_block_2_path_1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{65, 66, 67}));
        ArrayList<Integer> if_block_2_path_2 = new ArrayList<Integer>(Arrays.asList(new Integer[]{65, 66, 69}));
        if_block_2.put(if_block_2_range,
                new ArrayList<ArrayList<Integer>>(Arrays.asList(
                        if_block_2_path_1,
                        if_block_2_path_2)));

        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> else_block_2 = new HashMap<>();
        ArrayList<Integer> else_block_2_range = new ArrayList<Integer>(Arrays.asList(new Integer[]{71, 73}));
        ArrayList<Integer> else_block_2_path_1 = new ArrayList<Integer>(Arrays.asList(new Integer[]{72}));
        else_block_2.put(else_block_2_range,
                new ArrayList<ArrayList<Integer>>(Arrays.asList(
                        else_block_2_path_1)));

        Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths = new Stack<>();
        paths.push(if_block_1);
        paths.push(else_block_1);
        paths.push(if_block_2);
        paths.push(else_block_2);

        return paths;
    }
}
