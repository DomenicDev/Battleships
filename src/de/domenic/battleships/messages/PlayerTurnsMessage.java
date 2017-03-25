package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class PlayerTurnsMessage extends AbstractMessage {
    
    private int id;

    public PlayerTurnsMessage() {
    }

    public PlayerTurnsMessage(int id) {
        this.id = id;
    }

    /**
     * @return the id of the player who turns
     */
    public int getId() {
        return id;
    }    
    
}
