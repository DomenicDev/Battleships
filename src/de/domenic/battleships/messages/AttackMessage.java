package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Position;

/**
 *
 * @author Domenic
 */
@Serializable
public class AttackMessage extends AbstractMessage {
    
    private int attackedPlayerId;
    private Position pos;

    public AttackMessage() {
        
    }
    
    public AttackMessage(int id, Position pos) {
        this.attackedPlayerId = id;
        this.pos = pos;
    }
    
    public Position getPosition() {
        return pos;
    }

    public int getAttackedPlayerId() {
        return attackedPlayerId;
    }
    
}
