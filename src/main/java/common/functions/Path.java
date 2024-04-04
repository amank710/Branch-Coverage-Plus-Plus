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

    public Path ()
    {
        path = new java.util.ArrayList<>();
    }

    public List<Integer> getPath()
    {
        return path;
    }

    public void addLine(int line)
    {
        path.add(line);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.size(); i++)
        {
            sb.append(path.get(i));
            if (i < path.size() - 1)
            {
                sb.append(" -> ");
            }
        }
        return sb.toString();
    }
}
