package common;

import java.io.Serializable;
import java.util.*;

import common.util.Tuple;

public class PathCoverage implements Serializable
{
    private Map<String, List<Integer>> pathCoverageMetadata;
    private Map<String, Map<Integer, Integer>> lineHits;
    private Map<String, Set<List<Integer>>> uncoveredPaths;

    public PathCoverage(Map<String, Tuple<Integer, Integer>> pathCoverageMetadata, Map<String, Map<Integer, Integer>> lineHits, Map<String, Set<List<Integer>>> uncoveredPaths)
    {
        this.lineHits = lineHits;
        this.uncoveredPaths = uncoveredPaths;

        this.pathCoverageMetadata = new HashMap<>();
        for (Map.Entry<String, Tuple<Integer, Integer>> entry : pathCoverageMetadata.entrySet())
        {
            Tuple<Integer, Integer> tuple = entry.getValue();
            this.pathCoverageMetadata.put(entry.getKey(), Arrays.asList(tuple.first(), tuple.second()));
        }
    }


    public Map<String, List<Integer>> getPathMetadata()
    {
        return pathCoverageMetadata;
    }

    public Map<String, Map<Integer, Integer>> getLineHits()
    {
        return lineHits;
    }

    public Map<String, Set<List<Integer>>> getUncoveredPaths()
    {
        return uncoveredPaths;
    }

    @Override
    public String toString()
    {
        return "PathCoverage{" +
                "pathCoverageMetadata=" + pathCoverageMetadata + 
                ", lineHits=" + lineHits +
                ", uncoveredPaths=" + uncoveredPaths +
                '}';
    }
}
