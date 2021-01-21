package engine.algorithm;

import engine.graph.Graph;
import engine.interfaces.Algorithm;
import engine.tools.Vector;

import java.util.Stack;

public class DFS implements Algorithm {
    private Stack<Integer> internalStack;
    private boolean[] discovered;
    private int[] parenthood;
    private Graph g;
    private int stop;

    @Override
    public void initAlgorithm(Vector start, Vector stop) {
        this.discovered = new boolean[this.g.getNodes()];
        this.parenthood = new int[this.g.getNodes()];

        int start1 = Graph.vectorToNode(start, this.g.maxX);
        this.stop = Graph.vectorToNode(stop, this.g.maxX);
        this.internalStack = new Stack<>();
        this.internalStack.push(start1);
        this.discovered[start1] = true;
        for (int i = 0; i < this.g.getNodes(); ++i) {
            this.parenthood[i] = -1;
        }
    }

    @Override
    public Vector nextStep() {
        if (this.internalStack.isEmpty()) {
            return null;
        }
        int v = this.internalStack.pop();
        if (this.stop != v) {
            for (int u = 0; u < this.g.getNodes(); ++u) {
                if (g.hasEdge(v, u) && !this.discovered[u]) {
                    this.discovered[u] = true;
                    this.parenthood[u] = v;
                    this.internalStack.push(u);
                }
            }
        }
        return Graph.nodeToVector(v, this.g.maxX);
    }

    @Override
    public void loadGraph(Graph g) {
        this.g = g;
    }

    @Override
    public boolean[] getVisited() {
        return this.discovered;
    }

    @Override
    public int getParent(int node) {
        return this.parenthood[node];
    }
}
