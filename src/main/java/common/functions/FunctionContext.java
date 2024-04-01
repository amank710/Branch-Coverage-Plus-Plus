package common.functions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A FunctionContext corresponds to a single function that is Instrumentable.
 *
 * It contains all the Paths that can be reached in the function.
 */
public class FunctionContext
{
    Set<Path> paths;

    public FunctionContext() {
        paths = new HashSet<>();
    }
    public Set<Path> getPaths()
    {
        return paths;
    }

//    public void setPaths(Set<Path> paths) {
//        this.paths = paths;
//    }

    public void setPath(Path path) {
        this.paths.add(path);
    }
}
