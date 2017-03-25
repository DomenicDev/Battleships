package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class CloseGameMessage extends AbstractMessage {
    
    private String reason;

    public CloseGameMessage() {
    }

    public CloseGameMessage(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }    
}
