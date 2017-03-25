package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.service.serializer.ServerSerializerRegistrationsService;
import de.domenic.battleships.game.GameEventListener;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.NetworkUtils;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.game.State;
import de.domenic.battleships.messages.AttackMessage;
import de.domenic.battleships.messages.AttackResultMessage;
import de.domenic.battleships.messages.CloseGameMessage;
import de.domenic.battleships.messages.FinishedLoadingGameMessage;
import de.domenic.battleships.messages.GameDecisionMessage;
import de.domenic.battleships.messages.GameStartMessage;
import de.domenic.battleships.messages.JoinGameMessage;
import de.domenic.battleships.messages.LeaveGameMessage;
import de.domenic.battleships.messages.LoadGameMessage;
import de.domenic.battleships.messages.PlaceShipMessage;
import de.domenic.battleships.messages.PlayerTurnsMessage;
import de.domenic.battleships.messages.RequestPlacingMessage;
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
public class ServerAppState extends AbstractAppState implements MessageListener<HostedConnection>, ConnectionListener, GameEventListener {

    private Server server;
    private HostedConnection connOne, connTwo;
    private Player playerOne, playerTwo;
    private GameRuler gameRuler;
    private GameSettings gameSettings;

    private ConnectionListener listener;

    private AppStateManager stateManager;

    public GameSettings getGameSettings() {
        return gameSettings;
    }

    public Server getServer() {
        return server;
    }

    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }

    public ConnectionListener getListener() {
        return listener;
    }

    @Override
    public void onMiss(Player attackedPlayer, Position pos) {
        sendAttackResultMessage(attackedPlayer, false, pos);
    }

    @Override
    public void onShipHit(Player attackedPlayer, Position hitPosition) {
        sendAttackResultMessage(attackedPlayer, true, hitPosition);
    }

    @Override
    public void onShipDestroyed(Player attackedPlayer, Ship destroyedShip) {
        HostedConnection conn = getHostedConnectionByPlayer(attackedPlayer);
        ShipDestroyedMessage sdm = new ShipDestroyedMessage(conn.getId(), destroyedShip);
        server.broadcast(sdm);
    }

    @Override
    public void onShipPlaced(Player player, Ship placedShip) {
        HostedConnection conn = getHostedConnectionByPlayer(player);
        if (conn != null) {
            PlaceShipMessage pm = new PlaceShipMessage(placedShip);
            server.broadcast(Filters.equalTo(conn), pm);
        }
    }

    @Override
    public void onPlayerTurn(Player nextPlayer) {
        HostedConnection conn = getHostedConnectionByPlayer(nextPlayer);
        if (conn != null) {
            PlayerTurnsMessage ptm = new PlayerTurnsMessage(conn.getId());
            server.broadcast(ptm);
        }
    }

    @Override
    public void onGameDecided(Player playerWhoHasWon) {
        HostedConnection conn = getHostedConnectionByPlayer(playerWhoHasWon);
        if (conn != null) {
            server.broadcast(new GameDecisionMessage(conn.getId()));
        }
    }

    @Override
    public void onPlayerFinishedPlacing(Player finishedPlayer) {
        Player opponent = gameRuler.getOpponent(finishedPlayer);

        // when both players have set their ships, the game can start
        if (!opponent.hasPlacedAllShips()) {
            sendWaitingMessage(getHostedConnectionByPlayer(finishedPlayer), NetworkUtils.REASON_FOR_WAITING_1);
        }
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.stateManager = stateManager;
    }

    public Server createServer() {

        try {
            server = Network.createServer(5555);
            server.addConnectionListener(this);
            server.addMessageListener(this);

            if (server != null) {
                server.getServices().removeService(server.getServices().getService(ServerSerializerRegistrationsService.class));
            }

            server.start();

            playerOne = new Player();
            playerTwo = new Player();

        } catch (IOException ex) {
            Logger.getLogger(ServerAppState.class.getName()).log(Level.SEVERE, null, ex);
        }
        return server;
    }

    @Override
    public void update(float tpf) {

    }

    public void startGame(GameSettings settings) {
        if (connOne == null || connTwo == null) {
            return;
        }
        this.gameSettings = settings;
        gameRuler = new GameRuler(playerOne, playerTwo, this, gameSettings);
        stateManager.attach(gameRuler);
        server.broadcast(new LoadGameMessage(gameSettings));
        gameRuler.start();
    }

    @Override
    public void messageReceived(HostedConnection source, Message m) {

        if (m instanceof JoinGameMessage) {
            System.out.println("Server received JM");
            if (server.getConnections().size() <= 2) {
                JoinGameMessage jm = (JoinGameMessage) m;
                server.broadcast(new JoinGameMessage(source.getId(), jm.getPlayerName()));
                System.out.println("Hier");
                if (connOne == null) {
                    connOne = source;
                    playerOne.setName(jm.getPlayerName());
                } else if (connTwo == null) {
                    connTwo = source;
                    playerTwo.setName(jm.getPlayerName());
                    server.broadcast(Filters.equalTo(source), new JoinGameMessage(getHostedConnectionByPlayer(playerOne).getId(), playerOne.getName()));
                }
            } else {
                source.close("There are already too many players on this server!");
            }
        }

        // ---------- PlaceShipMessage --------------
        if (m instanceof RequestPlacingMessage && gameRuler.getState() == State.PLACING_SHIPS) {
            RequestPlacingMessage pm = (RequestPlacingMessage) m;
            Player player = getPlayerByHostedConnection(source);
            if (player != null) {
                Ship ship = getShipById(player, pm.getShipId());

                // check whether this ship has been moved already and if so don't apply this request
                if (ship != null && ship.getPositions() == null) {
                    gameRuler.placeShip(player, ship, pm.getPosition(), pm.getDirection());
                }
            }

        } else if (m instanceof AttackMessage) {
            AttackMessage am = (AttackMessage) m;
            Player player = getPlayerByHostedConnection(source);
            if (player != null && gameRuler.getActivePlayer() == player) {
                gameRuler.attack(am.getPosition());
            }

        } else if (m instanceof FinishedLoadingGameMessage) {
            Player player = getPlayerByHostedConnection(source);
            createAndSendShips(player, server, source);
            if (!gameSettings.isAutoSet()) {
                server.broadcast(Filters.equalTo(source), new StateChangedMessage(State.PLACING_SHIPS));
            } else {
                gameRuler.placeShipsRandomly(player);
            }
        
        
        } else if (m instanceof LeaveGameMessage) {
            // close the whole game when one player leaves
            if (source == connOne || source == connTwo) {
                server.broadcast(new CloseGameMessage("Player left the game."));
            }
        }
    }

    private Player getPlayerByHostedConnection(HostedConnection conn) {
        if (conn == connOne) {
            return playerOne;
        } else if (conn == connTwo) {
            return playerTwo;
        } else {
            return null;
        }
    }

    public HostedConnection getHostedConnectionByPlayer(Player player) {
        if (player == playerOne) {
            return connOne;
        } else if (player == playerTwo) {
            return connTwo;
        } else {
            return null;
        }
    }

    private Ship getShipById(Player player, int id) {
        for (Ship ship : player.getShips()) {
            if (ship.getId() == id) {
                return ship;
            }
        }
        return null;
    }

    @Override
    public void connectionAdded(Server server, HostedConnection conn) {
        System.out.println("CONNECTION ADDED!");
    }

    @Override
    public void connectionRemoved(Server server, HostedConnection conn) {
        System.out.println("Client connection removed");
        if (conn == connOne) {
            connOne = null;
        } else if (conn == connTwo) {
            connTwo = null;
        } 
    }

    private void sendWaitingMessage(HostedConnection conn, String reasonForWaiting) {
        FinishedPlacingMessage wm = new FinishedPlacingMessage(reasonForWaiting);
        server.broadcast(Filters.equalTo(conn), wm);
    }

    private void sendAttackResultMessage(Player attackedPlayer, boolean hit, Position pos) {
        HostedConnection conn = getHostedConnectionByPlayer(attackedPlayer);
        if (conn != null) {
            AttackResultMessage arm = new AttackResultMessage(conn.getId(), hit, pos);
            server.broadcast(arm);
        }
    }

    private void createAndSendShips(Player player, Server server, HostedConnection conn) {
        ShipArrayMessage sm = new ShipArrayMessage(player.getShips().toArray(new Ship[player.getShips().size()]));
        server.broadcast(Filters.equalTo(conn), sm);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (server != null && server.isRunning()) {
            server.close();
            System.out.println("close server");
        }
        connOne = null;
        connTwo = null;
        playerOne = null;
        playerTwo = null;
    }

    @Override
    public void onGameStart(Player firstPlayer) {
        HostedConnection conn = getHostedConnectionByPlayer(firstPlayer);
        if (conn != null) {
            GameStartMessage startMessage = new GameStartMessage(conn.getId());
            server.broadcast(startMessage);
        }

    }

    @Override
    public void onAttack(Player attackedPlayer, Position attackedPosition) {
        HostedConnection conn = getHostedConnectionByPlayer(attackedPlayer);
        AttackMessage am = new AttackMessage(conn.getId(), attackedPosition);
        server.broadcast(am);
    }

    @Override
    public void onStateChanged(State newState) {
        StateChangedMessage scm = new StateChangedMessage(newState);
        server.broadcast(scm);
    }

}
