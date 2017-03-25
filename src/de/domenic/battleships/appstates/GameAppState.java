package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.gui.HostGameScreenController;
import de.domenic.battleships.gui.LobbyScreenController;
import de.domenic.battleships.gui.Screen;
import de.domenic.battleships.messages.CloseGameMessage;
import de.domenic.battleships.messages.JoinGameMessage;
import de.domenic.battleships.messages.LeaveGameMessage;
import de.domenic.battleships.messages.LoadGameMessage;

/**
 * A GameAppState builds the interface between the GUI and the actual application.
 * From here server, clients and of course the actual game will be started.
 *
 * @author Domenic
 */
public class GameAppState extends AbstractAppState {

    private NiftyAppState niftyAppState;
    private Application app;
    private AppStateManager stateManager;
    private ServerAppState serverAppState;
    private ClientAppState clientAppState;
    
    private MainMenuSceneState mainMenuSceneState;

    private AbstractGame game;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.stateManager = stateManager;

        niftyAppState = new NiftyAppState(this);
        stateManager.attach(niftyAppState);
        
//        mainMenuSceneState = new MainMenuSceneState();
//        stateManager.attach(mainMenuSceneState);

        app.getInputManager().addMapping("Exit", new KeyTrigger(KeyInput.KEY_ESCAPE));
        app.getInputManager().addListener((ActionListener) (String name, boolean isPressed, float tpf) -> {
            if (isPressed) {
                if (stateManager.hasState(clientAppState) && clientAppState.isConnected()) {                    
                    clientAppState.getClient().send(new LeaveGameMessage(clientAppState.getID()));
                } else {
                    backToMainMenu();
                }
            }
        }, "Exit");
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanup() {
        super.cleanup();
        backToMainMenu();
    }

    public void quit() {
        app.stop();
    }

    public void startGame(GameSettings settings, boolean humanPlayer) {
        detachMainMenuScene();
        SingleplayerGame singleplayerGame = new SingleplayerGame(settings);
        singleplayerGame.setSecondPlayerNPC(!humanPlayer);
        stateManager.attach(singleplayerGame);
        niftyAppState.goToScreen("hud");
        this.game = singleplayerGame;
    }
    
    private void detachMainMenuScene() {
//        if (stateManager.hasState(mainMenuSceneState)) {
//            stateManager.detach(mainMenuSceneState);
//        }
    }

    public void startNetworkGame(GameSettings settings) {
        detachMainMenuScene();
        if (settings != null && serverAppState != null) {
            serverAppState.startGame(settings);
        }
    }

    public void setupClient() {
        clientAppState = new ClientAppState();
        stateManager.attach(clientAppState);
    }

    public void connect(String address, int port) {
        if (clientAppState.connect(address, port)) {            
            initMessageListener();
            clientAppState.start();
        }
    }

    public void setupServerAndClient() {
        serverAppState = new ServerAppState();
        stateManager.attach(serverAppState);
        serverAppState.createServer();

        clientAppState = new ClientAppState();
        stateManager.attach(clientAppState);
        if (clientAppState.connect("localhost", 5555)) {
            initMessageListener();
            clientAppState.start();
        }
    }

    public void backToMainMenu() {
        if (stateManager.hasState(clientAppState)) {
            stateManager.detach(clientAppState);
        }
        if (stateManager.hasState(serverAppState)) {
            stateManager.detach(serverAppState);
        }
        if (stateManager.hasState(game)) {
            stateManager.detach(game);
        }        

        // lod main menu scene
//        if (!stateManager.hasState(mainMenuSceneState)) {
//            stateManager.attach(mainMenuSceneState);
//        }
        
        // reset gui        
        niftyAppState.goToScreen(Screen.MainMenu.getIdName());
    }

    private void initMessageListener() {
        clientAppState.getClient().addMessageListener((Client source, Message m) -> {
            if (m instanceof JoinGameMessage) {

                System.out.println("Received JoinMessage");
                JoinGameMessage jm = (JoinGameMessage) m;

                if (jm.getId() == source.getId()) {
                    return;
                }

                    // if this is the host
                if (stateManager.hasState(serverAppState)) {
                    // another player has joint your game
                    HostGameScreenController controller = niftyAppState.getScreenController(HostGameScreenController.class);
                    System.out.println(controller);
                    controller.setEnemyName(jm.getPlayerName()); // refresh gui
                } else { // if this is a client
                    niftyAppState.goToScreen(Screen.LobbyScreen.getIdName());
                    app.enqueue(() -> {
                        niftyAppState.getScreenController(LobbyScreenController.class).setEnemyName(jm.getPlayerName());
                    });
                }

            } else if (m instanceof LoadGameMessage) {
                detachMainMenuScene();
                System.out.println("RECEIVED LOAD GAME MESSAGE");
                LoadGameMessage dgm = (LoadGameMessage) m;
                MultiplayerGame mpGame = new MultiplayerGame(dgm.getSettings());
                clientAppState.setGame(mpGame);
                mpGame.setClientAppState(clientAppState);
                game = mpGame;
                stateManager.attach(game);
                niftyAppState.goToScreen("hud");

            } else if (m instanceof CloseGameMessage) {
                System.out.println("Received CloseGameMessage");
                CloseGameMessage cm = (CloseGameMessage) m;
                // make popup
                backToMainMenu();
            }
        });

        clientAppState.getClient().addClientStateListener(new ClientStateListener() {
            @Override
            public void clientConnected(Client c) {
                System.out.println("I HAVE CONNECTED!");
                c.send(new JoinGameMessage(c.getId(), System.getProperty("user.name")));
            }

            @Override
            public void clientDisconnected(Client c, ClientStateListener.DisconnectInfo info) {
                System.out.println("I HAVE BEEN DISCONNECTED");
            //    backToMainMenu();
            }
        });

    }

}
