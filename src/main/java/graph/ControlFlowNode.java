package graph;

public abstract class ControlFlowNode
{
    // Neighbors are the nodes that will be executed after this node (the code block node or conditional node) is executed
    private ControlFlowNode[] neighbors;

    // Append a neighbor to the neighbors array
    abstract void visualizer(int depth);
}
