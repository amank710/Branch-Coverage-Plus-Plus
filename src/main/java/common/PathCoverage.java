package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import common.util.Tuple;

public class PathCoverage implements Serializable
{
    private Map<String, Tuple<Integer, Integer>> pathCoverageMetadata;
    private Map<String, Map<Integer, Integer>> lineHits;
    private Map<String, ArrayList<ArrayList<Integer>>> uncoveredPaths;

    public PathCoverage(Map<String, Tuple<Integer, Integer>> pathCoverageMetadata, Map<String, Map<Integer, Integer>> lineHits, Map<String, ArrayList<ArrayList<Integer>>> uncoveredPaths)
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

    public Map<String, ArrayList<ArrayList<Integer>>> getUncoveredPaths()
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
