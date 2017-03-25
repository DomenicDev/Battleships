package de.domenic.battleships.game;

import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Domenic
 */
@Serializable
public class GameEntity {
    private static int idCounter;
    private int id = getNextId();

    private static int getNextId() {return idCounter++;}

    public int getId() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        return o instanceof GameEntity && ((GameEntity)o).getId() == this.getId();
    }
    
    
    
}
