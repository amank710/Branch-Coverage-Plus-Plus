package graph;

public abstract class ControlFlowGraph
{
    private int[] lineNumbers;
    private ControlFlowGraph[] neighbors;

    void visualizer(int depth){
        for (ControlFlowGraph neighbor : neighbors) {
            neighbor.visualizer(depth);
        }
    }
}
