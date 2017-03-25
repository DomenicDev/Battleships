package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.AbstractMessage;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.game.State;
import de.domenic.battleships.messages.AttackMessage;
import de.domenic.battleships.messages.AttackResultMessage;
import de.domenic.battleships.messages.GameDecisionMessage;
import de.domenic.battleships.messages.GameStartMessage;
import de.domenic.battleships.messages.PlaceShipMessage;
import de.domenic.battleships.messages.PlayerTurnsMessage;
import de.domenic.battleships.messages.ShipArrayMessage;
import de.domenic.battleships.messages.ShipDestroyedMessage;
import de.domenic.battleships.messages.StateChangedMessage;
import de.domenic.battleships.messages.FinishedPlacingMessage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Domenic
 */
public class ClientAppState extends AbstractAppState implements MessageListener<Client> {

    private Client client;
    private AbstractGame game;
    private Player ownPlayer;
    private AppStateManager stateManager;

    private Application app; // used for callable, otherwise not thread safe

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
        this.app = app;
    }

    public boolean connect(String adress, int port) {
        try {
            this.client = Network.connectToServer(adress, port);
            this.client.addMessageListener(this);            
            return true;
        } catch (IOException ex) {
            Logger.getLogger(ClientAppState.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    public void start() {
        this.client.start();
    }

    public void sendMessage(AbstractMessage am) {
        if (am != null) {
            client.send(am);
        }
    }

    public void setGame(AbstractGame game) {
        this.game = game;
        this.ownPlayer = game.playerOne;
    }

    public int getID() {
        return client.getId();
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        if (client != null && client.isConnected()) {
            client.close();
        }
        super.cleanup();
    }

    @Override
    public void messageReceived(Client source, Message m) {
        if (m instanceof ShipArrayMessage) {
            app.enqueue(() -> {
                ShipArrayMessage sam = (ShipArrayMessage) m;
                ownPlayer.setShips(sam.getShips());

                if (!game.getSettings().isAutoSet()) {
                    game.letPlayerPlaceShips(ownPlayer);
                    game.enterCameraPlacingMode(ownPlayer);
                }
            });

        } else if (m instanceof PlaceShipMessage) {
            app.enqueue(() -> {
                PlaceShipMessage pm = (PlaceShipMessage) m;
                Ship ship;
                if ((ship = ownPlayer.getShip(pm.getPlacedShip().getId())) != null) {
                    ship.setPositions(pm.getPlacedShip().getPositions());
                    ship.setLength(pm.getPlacedShip().getLength());
                    ship.setDirection(pm.getPlacedShip().getDirection());
                    game.onShipPlaced(ownPlayer, ship);
                }
            });

        } else if (m instanceof FinishedPlacingMessage) {
            app.enqueue(() -> {
                FinishedPlacingMessage fm = (FinishedPlacingMessage) m;
                game.onStateChanged(State.WAITING);
                game.onPlayerFinishedPlacing(ownPlayer);                
                return null;
            });

        } else if (m instanceof StateChangedMessage) {
            app.enqueue(() -> {
                StateChangedMessage scm = (StateChangedMessage) m;
                game.onStateChanged(scm.getNewState());
                return null;
            });

        } else if (m instanceof PlayerTurnsMessage) {
            app.enqueue(() -> {
                PlayerTurnsMessage tm = (PlayerTurnsMessage) m;
                Player nextPlayer = getPlayer(tm.getId());
                game.onPlayerTurn(nextPlayer);
                return null;
            });

        } else if (m instanceof ShipDestroyedMessage) {
            app.enqueue(() -> {
                ShipDestroyedMessage sdm = (ShipDestroyedMessage) m;
                Player player = getPlayer(sdm.getAttackedPlayerId());
                player.addShip(sdm.getShip()); // neccessary ?
                game.onShipDestroyed(player, sdm.getShip());
                return null;
            });

        } else if (m instanceof AttackResultMessage) {
            app.enqueue(() -> {
                AttackResultMessage arm = (AttackResultMessage) m;
                Player player = getPlayer(arm.getAttackedPlayerId());
                Position pos = arm.getAttackedPos();
                if (arm.isHit()) {
                    game.onShipHit(player, pos);
                } else {
                    game.onMiss(player, pos);
                }
                return null;
            });

        } else if (m instanceof GameDecisionMessage) {
            app.enqueue(() -> {
                GameDecisionMessage gdm = (GameDecisionMessage) m;
                Player player = getPlayer(gdm.getWinnerId());
                game.onGameDecided(player);
                return null;
            });

        } else if (m instanceof GameStartMessage) {
            app.enqueue(() -> {
                GameStartMessage gm = (GameStartMessage) m;
                Player player = getPlayer(gm.getBeginnerID());
                game.onGameStart(player);
            });
        } else if (m instanceof AttackMessage) {
            app.enqueue(() -> {
                AttackMessage am = (AttackMessage) m;
                Player attackedPlayer = getPlayer(am.getAttackedPlayerId());
                Position attackedPos = am.getPosition();
                game.onAttack(attackedPlayer, attackedPos);
            });
        }

    }

    public Client getClient() {
        return client;
    }

    private Player getPlayer(int id) {
        return id == client.getId() ? ownPlayer : game.playerTwo;
    }   

    public void closeConnection() {
        if (client != null && client.isConnected()) {
            client.close();
        }
    }

    public boolean isConnected() {
       return client != null && client.isConnected();
    }

}
