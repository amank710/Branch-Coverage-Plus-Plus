package graph;

public abstract class ControlFlowNode
{
    private ControlFlowNode[] neighbors;
    abstract void visualizer(int depth);
}
