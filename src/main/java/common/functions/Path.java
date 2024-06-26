package common.functions;

import java.util.*;

/**
 * A Path corresponds to a code path in a FunctionContext.
 *
 * It corresponds to a set of line numbers.
 */
public class Path implements Iterable<Integer>
{
    private Set<Integer> path;

    public Path ()
    {
        path = new TreeSet<Integer>();
    }

    public Path(Set<Integer> path)
    {
        this.path = path;
    }

    public Collection<Integer> getPath()
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
        for (int line : path)
        {
            sb.append(line);
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return path.iterator();
    }
}
