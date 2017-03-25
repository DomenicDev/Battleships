package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class FinishedPlacingMessage extends AbstractMessage {
    
    private String reasonForWaiting;

    public FinishedPlacingMessage() {
    }

    public FinishedPlacingMessage(String reasonForWaiting) {
        this.reasonForWaiting = reasonForWaiting;
    }

    public String getReasonForWaiting() {
        return reasonForWaiting;
    }
    
}
