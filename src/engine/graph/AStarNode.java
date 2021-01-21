package engine.graph;

import java.util.Comparator;

public class AStarNode {
    private int f = 0;
    private int g = 0;
    private int n;

    public AStarNode(int n) {
        this.n = n;
    }

    public static final Comparator<AStarNode> comp = (l, r) -> l.getH() - r.getH();

    public void setF(int x) { this.f = x; }
    public void setG(int x) { this.g = x; }

    public int getF() { return this.f; }
    public int getN() { return this.n; }
    public int getH() { return this.f + this.g; }
}
