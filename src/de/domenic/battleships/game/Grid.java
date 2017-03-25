package de.domenic.battleships.game;

import java.util.ArrayList;

/**
 *
 * @author Domenic
 */
public class Grid {
    
    public static final int LENGTH = GameUtils.GAME_FIELD_LENGTH;
    
    private ArrayList<GridEventListener> listeners = new ArrayList<>();
    
    public enum Tile {
        
        /**
         * This tile has not been shoot at or influenced in any other way.
         */
        Empty,
        
        /**
         * Used to show where a ship is placed
         */
        Place,
        
        /**
         * Marked automatically e.g. when a ship gets destroyed the tiles around
         * the ship will be marked with this tile (because there can't be a ship).
         */
        LightBlue,
        
        /**
         * No hit
         */
        Green,
        
        /**
         * Hit a ship
         */
        Read;        
    }
    
    private final Tile[][] gameField;

    public Grid() {
        gameField = new Tile[LENGTH][LENGTH];
        initGameField();
    }
    
    private void initGameField() {
        for (int y = 0; y < LENGTH; y++) {
            for (int x = 0; x < LENGTH; x++) {
                gameField[y][x] = Tile.Empty;
            }
        }
    }
    
   private boolean areCoordinatesValid(int x, int y) {
       return x >= 0 && x < LENGTH && y >= 0 && y < LENGTH;
   }
   
   public void setTileAt(Position pos, Tile tile) {
       setTileAt(pos.getX(), pos.getY(), tile);
   }
    
    public void setTileAt(int x, int y, Tile tile) {
        if (areCoordinatesValid(x,y) && tile != null) {
            gameField[y][x] = tile;
            listeners.stream().forEach((listener) -> {
                listener.onTileChange(this, new Position(x,y), tile);
            });
        }
    }
    
    public Tile getTileAt(Position pos) {
        int x = pos.getX();
        int y = pos.getY();
        return getTileAt(x, y);
    }
    
    public Tile getTileAt(int x, int y) {
        if (x >= 0 && x < LENGTH && y >= 0 && y < LENGTH) {
            return gameField[y][x];
        }
        return null;
    }
    
    public int getLength() {
        return LENGTH;
    }
    
    public void addGridListener(GridEventListener listener) {
        listeners.add(listener);
    }
    
}
