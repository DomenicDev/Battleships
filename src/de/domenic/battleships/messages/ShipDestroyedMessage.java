package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Ship;

/**
 *
 * @author Domenic
 */
@Serializable
public class ShipDestroyedMessage extends AbstractMessage {
    
    private int attackedPlayerId;
    private Ship ship;

    public ShipDestroyedMessage() {
    }

    public ShipDestroyedMessage(int attackedPlayerId, Ship ship) {
        this.attackedPlayerId = attackedPlayerId;
        this.ship = ship;
    }

    public int getAttackedPlayerId() {
        return attackedPlayerId;
    }

    public Ship getShip() {
        return ship;
    }
        
}
