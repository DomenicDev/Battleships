package de.domenic.battleships.game;

import de.domenic.battleships.game.GameEntity;
import de.domenic.battleships.game.Direction;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class Ship extends GameEntity {
    
    private Position[] positions;
    private int length;
    private int hitParts;
    private Direction direction;

    public Ship() {
    }

    public void setPositions(Position[] positions) {
        this.positions = positions;
    }
    
    public void setLength(int length) {
        this.length = length;
    }

    public Ship(Position[] positions) {
        if (positions == null || positions.length == 0) {
            throw new IllegalArgumentException("You need to have at least 1 position for a ship!");
        }
        this.positions = positions;
    }
    
    public boolean checkHit(Position pos) {
        for (Position p : positions) {
            if (p.equals(pos)) {
                hitParts++;
                return true;
            }
        }
        return false;
    }    

    public Position[] getPositions() {
        return positions;
    }    
    
    public int getLength() {
        return length;
    }

    /**
     * @return the destroyed
     */
    public boolean isDestroyed() {
        return hitParts == length;
    }   

    /**
     * @return the direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }
    
}
