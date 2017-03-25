package de.domenic.battleships.game;

import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public enum Direction {
    
    Left(-1, 0),
    Right(1, 0),
    Up(0, -1),
    Down(0, 1);   
    
    private Direction(int xDiff, int yDiff) {
        this.xDiff = xDiff;
        this.yDiff = yDiff;
    }
    
    private final int xDiff, yDiff;

    /**
     * @return the xDiff
     */
    public int getXDiff() {
        return xDiff;
    }

    /**
     * @return the yDiff
     */
    public int getYDiff() {
        return yDiff;
    }
    
}
