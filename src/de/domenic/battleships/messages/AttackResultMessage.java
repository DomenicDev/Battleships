package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Position;

/**
 *
 * @author Domenic
 */
@Serializable
public class AttackResultMessage extends AbstractMessage {
    
    private int attackedPlayerId;
    private boolean hit;
    private Position attackedPos;

    public AttackResultMessage() {
    }

    public AttackResultMessage(int attackerId, boolean hit, Position attackedPos) {
        this.attackedPlayerId = attackerId;
        this.hit = hit;
        this.attackedPos = attackedPos;
    }

    public Position getAttackedPos() {
        return attackedPos;
    }

    public int getAttackedPlayerId() {
        return attackedPlayerId;
    }

    public boolean isHit() {
        return hit;
    }
    
}
