package de.domenic.battleships.appstates;

import com.jme3.app.state.AbstractAppState;
import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.GameEventListener;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Grid;
import de.domenic.battleships.game.Grid.Tile;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.game.State;

/**
 *
 * @author Domenic
 */
public class GameRuler extends AbstractAppState {

    private final Player playerOne, playerTwo;
    private Player playerWhoIsTurn;

    private GameEventListener listener;
    private GameSettings settings;
    private State state = State.WAITING;

    private WaitingTask waitingTask;

    /**
     *
     * @param playerOne
     * @param playerTwo
     * @param listener
     * @param settings
     */
    public GameRuler(Player playerOne, Player playerTwo, GameEventListener listener, GameSettings settings) {
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.listener = listener;
        this.settings = settings;

        this.playerOne.setShips(GameUtils.createShips());
        this.playerTwo.setShips(GameUtils.createShips());
    }

    public void setSettings(GameSettings settings) {
        this.settings = settings;
    }

    public GameSettings getSettings() {
        return settings;
    }

    public void setListener(GameEventListener listener) {
        this.listener = listener;
        setState(State.PLACING_SHIPS);
    }

    public GameEventListener getListener() {
        return listener;
    }

    public State getState() {
        return state;
    }

    public final void setActivePlayer(Player player) {
        if (player != playerOne && player != playerTwo) {
            return;
        }
        if (playerWhoIsTurn != player) {
            listener.onPlayerTurn(player);
        }
        playerWhoIsTurn = player;
    }

    public void attack(final Position attackedPosition) {
        if (!GameUtils.isValidPosition(attackedPosition) || state != State.GAME_RUNNING || waitingTask != null) {
            return;
        }        

        Player attackedPlayer = playerWhoIsTurn == playerOne ? playerTwo : playerOne;

        // check whether there has already been shot at and if so this shot is invalid
        Tile tile = attackedPlayer.getGameField().getGrid().getTileAt(attackedPosition);
        if (tile == Tile.Read || tile == Tile.Green) {
            return;
        }
        
        listener.onAttack(attackedPlayer, attackedPosition);

        // wait some seconds for shoot effect
        if (settings.isShootEffectsEnabled()) {
            waitingTask = new WaitingTask(() -> {
                waitingTask = null;
                handleAttack(attackedPlayer, attackedPosition);                
            });
            waitingTask.setWaitingDuration(GameUtils.FLIGHT_DURATION);
        } else {
            handleAttack(attackedPlayer, attackedPosition);
        }
    }

    private void handleAttack(Player attackedPlayer, Position attackedPosition) {
        // look if you hit a ship
        for (Ship ship : attackedPlayer.getShips()) {
            if (ship.checkHit(attackedPosition)) { // when hit, return true!
                attackedPlayer.getGameField().getGrid().setTileAt(attackedPosition, Grid.Tile.Read);
                listener.onShipHit(attackedPlayer, attackedPosition);

                // look if the ship has been destroyed by this attack
                if (ship.isDestroyed()) {
                    listener.onShipDestroyed(attackedPlayer, ship);

                    // check whether we have a winner
                    if (attackedPlayer.getNumberOfAliveShips() == 0) {
                        Player winner = attackedPlayer == playerOne ? playerTwo : playerOne;
                        listener.onGameDecided(winner);
                        setState(State.GAME_DECIDED);
                    }
                }
                return; // you made a hit, so return
            }
        }
        
        // if we arrive here, the player has missed
        attackedPlayer.getGameField().getGrid().setTileAt(attackedPosition, Grid.Tile.Green);
        listener.onMiss(attackedPlayer, attackedPosition);

        nextPlayerTurns(); // player missed, so other players turn now
    }

    private void nextPlayerTurns() {
        // it's the other players turn now        
        playerWhoIsTurn = playerWhoIsTurn == playerOne ? playerTwo : playerOne;
        listener.onPlayerTurn(playerWhoIsTurn);
    }

    /**
     * @return the player who actually is turned
     */
    public Player getActivePlayer() {
        return playerWhoIsTurn;
    }

    /**
     * Used for placing ships
     *
     * @param player
     * @return
     */
    public Ship getNextShipForPlayer(Player player) {
        return player.getNextShip();
    }

    public Player getOpponent(Player player) {
        if (player != null) {
            return player == playerOne ? playerTwo : playerOne;
        } else {
            return null;
        }
    }

    public Position[] placeShip(Player player, Ship ship, Position topPos, Direction dir) {
        if (state != State.PLACING_SHIPS) {
            return null;
        }
        Position[] pos = GameUtils.canShipBePlaced(player, ship, topPos, dir);
        if (pos != null) {
            ship.setPositions(pos);
            ship.setDirection(dir);
            listener.onShipPlaced(player, ship);
            if (player.hasPlacedAllShips()) {
                listener.onPlayerFinishedPlacing(player);
                if (getOpponent(player).hasPlacedAllShips()) {
                    startGame();
                }
            }
            return pos;
        } else {
            return null;
        }
    }

    public void start() {
        if (this.settings.isAutoSet()) {
            state = State.PLACING_SHIPS; // we need to set this, to call placeShip() 
         //   placeShipsRandomly(playerOne);
         //   placeShipsRandomly(playerTwo);
             
        } else {
            setState(State.PLACING_SHIPS);
        }
        
    }

    /**
     * Will pick a random beginner and let him do the first round
     */
    private void startGame() {
       
        //  Player beginner = Math.random() < 0.5 ? playerOne : playerTwo;
        Player beginner = playerOne; // this player has to be human
        playerWhoIsTurn = beginner;
        listener.onGameStart(beginner);
        setState(State.GAME_RUNNING);
    }

    private void setState(State newState) {
        this.state = newState;
        listener.onStateChanged(newState);
    }

    public final void placeShipsRandomly(Player player) {
        Ship nextShip;
        while ((nextShip = player.getNextShip()) != null) {
            boolean successfulPlaced = false;
            do {
                Direction dir = randomDirection();
                Position pos = randomPosition();

                if (placeShip(player, nextShip, pos, dir) != null) {
                    successfulPlaced = true;
                }
            } while (!successfulPlaced);

        }
    }

    private Position randomPosition() {
        int x = (int) Math.round(Math.random() * GameUtils.GAME_FIELD_LENGTH);
        int y = (int) Math.round(Math.random() * GameUtils.GAME_FIELD_LENGTH);

        return new Position(x, y);
    }

    private Direction randomDirection() {
        double d = Math.random();
        if (d >= 0 && d < 0.25) {
            return Direction.Up;
        }
        if (d >= 0.25 && d < 0.50) {
            return Direction.Left;
        }
        if (d >= 0.50 && d < 0.75) {
            return Direction.Down;
        }
        if (d >= 0.75 && d < 1.0) {
            return Direction.Right;
        }
        return Direction.Up; // should never arrive here 
    }

    private class WaitingTask {

        private float waitingDuration;
        private float currentPastedTime;
        private boolean done;

        private final WaitingListener listener;

        public WaitingTask(WaitingListener listener) {
            this.listener = listener;
        }

        public void setWaitingDuration(float waitingDuration) {
            this.waitingDuration = waitingDuration;
        }

        public void reset() {
            currentPastedTime = 0;
        }

        public void update(float tpf) {
            if (!done && (currentPastedTime += tpf) >= waitingDuration) {
                done = true;
                listener.onWaitingEnd();
            }
        }

    }

    private interface WaitingListener {

        void onWaitingEnd();

    }

    @Override
    public void update(float tpf) {
        
        if (waitingTask != null) {
            waitingTask.update(tpf);
        }
    }

}
