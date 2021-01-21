package engine.generators;

import engine.tools.Vector;

import java.util.*;

public class MazeGenerators {
    public static List<Vector> getTaxiNeighbourhood(Vector v, int w, int h) {
        List<Vector> ret = new ArrayList<>();
        for (int x = Math.max(v.x - 1, 0); x <= Math.min(v.x+1, w-1); ++x) {
            for (int y = Math.max(v.y - 1, 0); y <= Math.min(v.y+1, h-1); ++y) {
                if (Math.abs(v.x - x) + Math.abs(v.y - y) == 1) {
                    ret.add(new Vector(x, y));
                }
            }
        }
        return ret;
    }

    public static boolean[][] primGenerator(int width, int height, Vector start) {
        Random mazeGen = new Random();
        boolean[][] map = new boolean[width][height];
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                map[x][y] = true;
            }
        }

        map[start.x][start.y] = false;

        List<Vector> wallList = new ArrayList<>(MazeGenerators.getTaxiNeighbourhood(start, width, height));

        while(!wallList.isEmpty()) {
            int newWall = mazeGen.nextInt(wallList.size());
            Vector currentWall = wallList.get(newWall);
            wallList.remove(newWall);
            int freeSides = 0;
            for (Vector v : MazeGenerators.getTaxiNeighbourhood(currentWall, width, height)) {
                if (!map[v.x][v.y]) {
                    freeSides++;
                }
            }
            if (freeSides == 1) {
                map[currentWall.x][currentWall.y] = false;
                for(Vector u : MazeGenerators.getTaxiNeighbourhood(currentWall, width, height)) {
                    if (map[u.x][u.y]) {
                        wallList.add(u);
                    }
                }
            }
        }
        return map;
    }

    static List<Vector> getDFSNeighborhood(Vector v, int width, int height) {
        List<Vector> ret = new ArrayList<>();
        if (v.x+2 < width) {
            ret.add(new Vector(v.x+2, v.y));
        } if (v.y+2 < height) {
            ret.add(new Vector(v.x, v.y+2));
        } if (v.y-2 >= 0) {
           ret.add(new Vector(v.x, v.y-2));
        } if (v.x-2 >= 0) {
            ret.add(new Vector(v.x-2, v.y));
        }
        return ret;
    }

    static Vector avg(Vector a, Vector b) {
        return new Vector((int)(a.x + b.x) / 2, (int)(a.y + b.y) / 2);
    }

    public static boolean[][] rdfsGenerator(int width, int height, Vector start) {
        Stack<Vector> dfsStack = new Stack<>();
        List<Vector> internalList;
        boolean[][] visited = new boolean[width][height];
        boolean[][] map = new boolean[width][height];
        for(int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                map[x][y] = true;
            }
        }
        Random r = new Random();

        dfsStack.add(start);
        map[start.x][start.y] = false;
        visited[start.x][start.y] = true;

        while(!dfsStack.isEmpty()) {
            Vector current = dfsStack.pop();
            internalList = getDFSNeighborhood(current, width, height);
            Collections.shuffle(internalList);
            for (Vector v : internalList) {
                if(!visited[v.x][v.y]) {
                    visited[v.x][v.y] = true;
                    map[v.x][v.y] = false;
                    map[avg(current, v).x][avg(current, v).y] = false;
                    dfsStack.add(v);
                }
            }
        }

        return map;
    }
}
