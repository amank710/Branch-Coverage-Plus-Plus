package common.functions;

import java.util.ArrayList;
import java.util.List;

/**
 * A Path corresponds to a code path in a FunctionContext.
 *
 * It corresponds to a set of line numbers.
 */
public class Path
{
    private List<Integer> path;

    public Path()
    {
        path = new ArrayList<>();
    }

    public void add(int lineNumber)
    {
        path.add(lineNumber);
    }

    List<Integer> getPath()
    {
        return path;
    } 

    @Override
    public String toString()
    {
        return path.toString();
    }
}
