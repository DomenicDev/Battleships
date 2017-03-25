package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class LeaveGameMessage extends AbstractMessage {
    
    private int id;

    public LeaveGameMessage() {
    }

    public LeaveGameMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }    
}
