package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.State;

/**
 *
 * @author Domenic
 */
@Serializable
public class StateChangedMessage extends AbstractMessage {
    
    private State newState;

    public StateChangedMessage() {
    }

    public StateChangedMessage(State newState) {
        this.newState = newState;
    }

    public State getNewState() {
        return newState;
    }
}
