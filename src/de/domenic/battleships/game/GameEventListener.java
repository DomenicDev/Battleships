package de.domenic.battleships.game;

//import de.domenic.battleships.Grid.Tile;

/**
 *
 * @author Domenic
 */
public interface GameEventListener {
    
    void onMiss(Player attackedPlayer, Position pos);
    
    void onShipHit(Player attackedPlayer, Position hitPosition);
    
    void onShipDestroyed(Player attackedPlayer, Ship destroyedShip);
    
    void onShipPlaced(Player player, Ship placedShip);
    
//    void onTileChange(Player player, Position pos, Tile newTile);
    
    void onPlayerTurn(Player nextPlayer);
    
    void onStateChanged(State newState);
    
    /**
     * This is called when a player attacks. 
     * You can use this to play some attack effects
     * @param attackedPlayer
     * @param attackedPosition
     */
    void onAttack(Player attackedPlayer, Position attackedPosition);
    
    void onGameStart(Player firstPlayer);
    
    /**
     * This method is called when all players have set their ships
     * @param finishedPlayer
     */
    void onPlayerFinishedPlacing(Player finishedPlayer);
    
    void onGameDecided(Player playerWhoHasWon);
    
}
