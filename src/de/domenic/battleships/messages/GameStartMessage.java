package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class GameStartMessage extends AbstractMessage {
    
    private int beginnerID;

    public GameStartMessage() {
    }

    public GameStartMessage(int beginnerID) {
        this.beginnerID = beginnerID;
    }

    public int getBeginnerID() {
        return beginnerID;
    }
        
}
