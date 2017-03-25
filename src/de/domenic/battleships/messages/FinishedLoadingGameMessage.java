/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.domenic.battleships.messages;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class FinishedLoadingGameMessage extends AbstractMessage {

    private int id;
    
    public FinishedLoadingGameMessage() {
    }

    public FinishedLoadingGameMessage(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }    
}
