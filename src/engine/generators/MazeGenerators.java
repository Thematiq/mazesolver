package engine.generators;

import engine.tools.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
}
