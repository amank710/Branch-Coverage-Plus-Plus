package common;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import common.util.Tuple;

public class PathCoverage implements Serializable
{
    private Map<String, Tuple<Integer, Integer>> pathCoverageMetadata;
    private Map<String, Map<Integer, Integer>> lineHits;
    private Map<String, Set<List<Integer>>> uncoveredPaths;

    public PathCoverage(Map<String, Tuple<Integer, Integer>> pathCoverageMetadata, Map<String, Map<Integer, Integer>> lineHits, Map<String, Set<List<Integer>>> uncoveredPaths)
    {
        this.pathCoverageMetadata = pathCoverageMetadata;
        this.lineHits = lineHits;
        this.uncoveredPaths = uncoveredPaths;
    }

    public Map<String, Tuple<Integer, Integer>> getPathCoverageMetadata()
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
