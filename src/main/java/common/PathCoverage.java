package common;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

public class PathCoverage implements Serializable
{
    private double pathCoverageScore;
    private Map<String, Map<Integer, Integer>> lineHits;
    private Map<String, ArrayList<ArrayList<Integer>>> uncoveredPaths;

    public PathCoverage(double pathCoverageScore, Map<String, Map<Integer, Integer>> lineHits, Map<String, ArrayList<ArrayList<Integer>>> uncoveredPaths)
    {
        this.pathCoverageScore = pathCoverageScore;
        this.lineHits = lineHits;
        this.uncoveredPaths = uncoveredPaths;
    }

    public double getPathCoverageScore()
    {
        return pathCoverageScore;
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
                "pathCoverageScore=" + pathCoverageScore +
                ", lineHits=" + lineHits +
                ", uncoveredPaths=" + uncoveredPaths +
                '}';
    }
}
