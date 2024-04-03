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

    private void writeObject(ObjectOutputStream out) throws IOException
    {
        out.writeDouble(pathCoverageScore);
        out.writeObject(lineHits);
        out.writeObject(uncoveredPaths);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        pathCoverageScore = in.readDouble();
        lineHits = (Map<String, Map<Integer, Integer>>) in.readObject();
        uncoveredPaths = (Map<String, ArrayList<ArrayList<Integer>>>) in.readObject();
    }
}
