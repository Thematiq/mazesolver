package engine.interfaces;

import engine.graph.Graph;
import engine.tools.Vector;

public interface Algorithm {
    void initAlgorithm(Vector start, Vector stop);
    Vector nextStep();
    void loadGraph(Graph g);
    boolean[] getVisited();
    int getParent(int node);
}
