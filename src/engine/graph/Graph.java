package engine.graph;

import engine.tools.Vector;

import java.util.Map;

public class Graph {
    private final boolean[][] matrix;
    public int maxX = 0;
    private int nodes;

    public Graph(int nodes) {
        this.nodes = nodes;
        this.matrix = new boolean[nodes][nodes];
    }

    public void addEdge(int from, int to) {
        this.matrix[from][to] = true;
    }

    public void addDiEdge(int from, int to) {
        this.matrix[from][to] = true;
        this.matrix[to][from] = true;
    }

    public static int vectorToNode(Vector pos, int maxX) {
        return pos.x * maxX + pos.y;
    }

    public static Vector nodeToVector(int node, int maxX) {
        return new Vector( node / maxX, node % maxX);
    }

    public boolean hasEdge(int from, int to) {
        return this.matrix[from][to];
    }

    public static Graph getFromHashMap(boolean[][] map, int maxX, int maxY) {
        Graph g = new Graph(maxX * maxY);
        g.maxX = maxX;
        for (int y = 0; y < maxY; ++y) {
            for (int x = 0; x < maxX; ++x) {
                for (int w = Math.max(x-1, 0); w <= Math.min(x+1, maxX-1); w++) {
                    for (int h = Math.max(y-1, 0); h <= Math.min(y+1, maxY-1); h++) {
                        if (!map[x][y] && !map[w][h] && Math.abs(x - w) + Math.abs(y - h) <= 1 && ((x != w) || (y != h))) {
                            g.addEdge(vectorToNode(new Vector(x, y), maxX), vectorToNode(new Vector(w, h), maxX));
                        }
                    }
                }
            }
        }
        return g;
    }

    public int getNodes() { return this.nodes; }
}
