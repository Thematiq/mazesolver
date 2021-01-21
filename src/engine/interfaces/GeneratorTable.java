package engine.interfaces;

import engine.generators.MazeGenerators;
import engine.tools.Vector;

public enum GeneratorTable {
    PRIM;
    public boolean[][] getMap(int width, int hegiht, Vector start) {
        return switch(this) {
          case PRIM -> MazeGenerators.primGenerator(width, hegiht, start);
        };
    }

    public static GeneratorTable fromString(String name) {
        return switch(name.toLowerCase()) {
            case "prim" -> PRIM;
            default -> PRIM;
        };
    }
}
