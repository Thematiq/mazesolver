package engine.algorithm;

import engine.graph.Graph;
import engine.graph.AStarNode;
import engine.interfaces.Algorithm;
import engine.tools.Vector;

import java.util.PriorityQueue;

public class AStar implements Algorithm {
    private Graph g;
    private boolean[] discovered;
    private int[] parenthood;
    private PriorityQueue<AStarNode> list = new PriorityQueue<>(AStarNode.comp);
    private int stop;
    private Vector vecStop;


    @Override
    public void initAlgorithm(Vector start, Vector stop) {
        this.discovered = new boolean[this.g.getNodes()];
        this.parenthood = new int[this.g.getNodes()];
        for (int i = 0; i < this.g.getNodes(); ++i) {
            this.parenthood[i] = -1;
        }

        AStarNode v = new AStarNode(Graph.vectorToNode(start, g.maxX));
        v.setF(0);
        v.setG(start.getTaxiDist(stop));
        //v.setG(start.getEuclidean(stop));

        this.vecStop = stop;
        this.stop = Graph.vectorToNode(stop, g.maxX);
        this.list.add(v);
        this.discovered[v.getN()] = true;
    }

    @Override
    public Vector nextStep() {
        if (this.list.isEmpty()) {
            return null;
        }
        AStarNode v = this.list.remove();
        if (v.getN() != this.stop) {
            for (int u = 0; u < this.g.getNodes(); ++u) {
                if (g.hasEdge(v.getN(), u) && !this.discovered[u]) {
                    this.discovered[u] = true;
                    this.parenthood[u] = v.getN();
                    AStarNode uu = new AStarNode(u);
                    uu.setF(v.getF() + 1);
                    uu.setG(Graph.nodeToVector(u, this.g.maxX).getTaxiDist(this.vecStop));
                    //uu.setG(Graph.nodeToVector(u, this.g.maxX).getEuclidean(this.vecStop));
                    this.list.add(uu);
                }
            }
        }
        System.out.println("Current node: " + v.getN());
        return Graph.nodeToVector(v.getN(), this.g.maxX);
    }

    @Override
    public void loadGraph(Graph g) { this.g = g; }

    @Override
    public boolean[] getVisited() { return this.discovered; }

    @Override
    public int getParent(int node) { return this.parenthood[node]; }
}
