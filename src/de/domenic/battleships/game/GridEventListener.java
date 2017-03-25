
package de.domenic.battleships.game;

import de.domenic.battleships.game.Grid.Tile;

/**
 *
 * @author Domenic
 */
public interface GridEventListener {
    
    void onTileChange(Grid grid, Position pos, Tile newTile);
    
}
