import java.util.List;

/**
 * A Path corresponds to a code path in a FunctionContext.
 *
 * It corresponds to a set of line numbers.
 */
class Path
{
    private List<Integer> path;

    List<Integer> getPath()
    {
        return path;
    } 
}
