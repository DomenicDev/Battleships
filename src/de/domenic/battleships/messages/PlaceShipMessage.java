package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.Ship;

/**
 *
 * @author Domenic
 */
@Serializable
public class PlaceShipMessage extends AbstractMessage {
    
    private Ship placedShip;

    public PlaceShipMessage() {
    }

    public PlaceShipMessage(Ship placedShip) {
        this.placedShip = placedShip;
    }

    public Ship getPlacedShip() {
        return placedShip;
    }   
    
}
