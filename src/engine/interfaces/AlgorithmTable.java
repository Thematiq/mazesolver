package engine.interfaces;

import engine.algorithm.AStar;
import engine.algorithm.BFS;
import engine.algorithm.DFS;

public enum AlgorithmTable {
    BFS,
    DFS,
    ASTAR;

    public Algorithm getAlgorithm() {
        return switch(this) {
            case BFS -> new BFS();
            case DFS -> new DFS();
            case ASTAR -> new AStar();
        };
    }

    public static AlgorithmTable fromString(String param) {
        return switch (param.toLowerCase()) {
            case "astar" -> ASTAR;
            case "bfs" -> BFS;
            case "dfs" -> DFS;
            default -> BFS;
        };
    }
}
