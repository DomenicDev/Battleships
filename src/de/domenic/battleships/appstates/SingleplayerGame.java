package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import de.domenic.battleships.game.ComputerAI;
import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;

/**
 *
 * @author Domenic
 */
public class SingleplayerGame extends AbstractGame {

    private GameRuler gameRuler;
    private boolean secondPlayerNPC;
    private ComputerAI computerAI; // only used when played against pc
    
    private float waitingTimer; // only used when played against pc

    public SingleplayerGame(GameSettings settings) {
        super(settings);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        gameRuler = new GameRuler(playerOne, playerTwo, this, getSettings());
        stateManager.attach(gameRuler);
        gameRuler.setListener(this);
        
        app.getCamera().setLocation(new Vector3f(0, 20, 0));
        app.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        
    }

    private void letComputerAttack() {
        Position attackPos = computerAI.attack();
        gameRuler.attack(attackPos);
    }

    @Override
    public void onPlayerFinishedPlacing(Player finishedPlayer) {
        super.onPlayerFinishedPlacing(finishedPlayer);
        // when we play against a human, both fields will get deleted
        if (!isSecondPlayerNPC()) {
            visualAppState.removeAllShips();
            visualAppState.getVisualGameFieldByPlayer(finishedPlayer).setVisible(false);
        } else {
            visualAppState.getVisualGameFieldByPlayer(playerTwo).setVisible(false);
        }
        if (!gameRuler.getOpponent(finishedPlayer).hasPlacedAllShips()) {

            // in the case the seconds player is a computer, place ships randomly
            if (gameRuler.getOpponent(finishedPlayer) == playerTwo && isSecondPlayerNPC()) {
                gameRuler.placeShipsRandomly(gameRuler.getOpponent(finishedPlayer));
            } else { // otherwise let the second player place its ships
                letPlayerPlaceShips(gameRuler.getOpponent(finishedPlayer));
            }
        }
    }

    @Override
    public void onShipPlaced(Player player, Ship placedShip) {
        // we wont show the ships of the npc
        if (player == playerTwo && isSecondPlayerNPC()) {
            return;
        }
        super.onShipPlaced(player, placedShip);
    }   

    /**
     * @return the secondPlayerNPC
     */
    public boolean isSecondPlayerNPC() {
        return secondPlayerNPC;
    }

    /**
     * @param secondPlayerNPC the secondPlayerNPC to set
     */
    public void setSecondPlayerNPC(boolean secondPlayerNPC) {
        this.secondPlayerNPC = secondPlayerNPC;

        if (secondPlayerNPC) {
            computerAI = new ComputerAI(playerOne.getGameField().getGrid());
            playerTwo.setName("Computer");
        }

    }

    @Override
    protected void requestPlacing(Ship shipToPlace, Position placePos, Direction dir) {
        gameRuler.placeShip(activePlayer, shipToPlace, placePos, dir);
    }

    @Override
    protected void requestShoot(Position attackedPosition) {
        if (isSecondPlayerNPC() && activePlayer == playerTwo) {
            return;
        }
        gameRuler.attack(attackedPosition);        
    }

    @Override
    public void onFinishedLoadingGame() {
        // set new player names        
        if (isSecondPlayerNPC()) {
            playerOne.setName(System.getProperty("user.name"));
            playerTwo.setName("Computer");
        }
        
        gameRuler.start();

        // let player place their ships if neccessary
        if (!getSettings().isAutoSet()) {
            letPlayerPlaceShips(playerOne);
            activePlayer = playerOne;
        } else {
            gameRuler.placeShipsRandomly(playerOne);
            gameRuler.placeShipsRandomly(playerTwo);
        }
    }

    @Override
    public void onShipDestroyed(Player attackedPlayer, Ship destroyedShip) {
        super.onShipDestroyed(attackedPlayer, destroyedShip); 
        if (secondPlayerNPC && attackedPlayer == playerOne) {
            computerAI.onShipDestroyed();
        }
    }
    
    

    @Override
    public void update(float tpf) {
        super.update(tpf); 
        
        if (isSecondPlayerNPC() && activePlayer == playerTwo) {
            waitingTimer += tpf;
            if (waitingTimer > 2) {
                waitingTimer = 0;
                letComputerAttack();
            }
        }        
    }
}
