package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Ship;

/**
 *
 * @author Domenic
 */
@Serializable
public class ShipArrayMessage extends AbstractMessage {
    
    private Ship[] ships;
    
    public ShipArrayMessage() {
    }
    
    public ShipArrayMessage(Ship[] ships) {
        this.ships = ships;
    }

    public Ship[] getShips() {
        return ships;
    }    
}
