import java.util.Set;

/**
 * A FunctionContext corresponds to a single function that is Instrumentable.
 *
 * It contains all the Paths that can be reached in the function.
 */
class FunctionContext
{
    Set<Path> paths;

    Set<Path> getPaths()
    {
        return paths;
    }
}
