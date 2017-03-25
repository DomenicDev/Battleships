/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.game.GameSettings;

/**
 *
 * @author Domenic
 */
@Serializable
public class LoadGameMessage extends AbstractMessage {
    
    private GameSettings settings;

    public LoadGameMessage() {
    }

    public LoadGameMessage(GameSettings settings) {
        this.settings = settings;
    }

    public GameSettings getSettings() {
        return settings;
    }
    
}
