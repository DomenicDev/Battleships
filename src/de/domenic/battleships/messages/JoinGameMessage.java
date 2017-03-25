package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class JoinGameMessage extends AbstractMessage {
    
    private int id;
    private String playerName;

    public JoinGameMessage() {
    }

    public JoinGameMessage(int id, String playerName) {
        this.id = id;
        this.playerName = playerName;
    }

    public int getId() {
        return id;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
}
