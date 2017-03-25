package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.Position;

/**
 *
 * @author Domenic
 */
@Serializable
public class RequestPlacingMessage extends AbstractMessage {
    
    private int shipID;
    private Position pos;
    private Direction dir;
    
    public RequestPlacingMessage() {
        
    }
    
    public RequestPlacingMessage(int shipID, Position pos, Direction dir) {
        this.shipID = shipID;
        this.pos = pos;
        this.dir = dir;
    }

    public Position getPosition() {
        return pos;
    }
    
    public int getShipId() {
        return this.shipID;
    }

    public Direction getDirection() {
        return dir;
    }
    
    
}
