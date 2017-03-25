package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class GameDecisionMessage extends AbstractMessage {
    
    private int winnerId;

    public GameDecisionMessage() {
    }

    public GameDecisionMessage(int winnerId) {
        this.winnerId = winnerId;
    }

    public int getWinnerId() {
        return winnerId;
    }    
    
}
