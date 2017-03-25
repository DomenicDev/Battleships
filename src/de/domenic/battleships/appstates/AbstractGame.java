package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Vector3f;
import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.GameEventListener;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Grid.Tile;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.game.State;
import de.domenic.battleships.gui.HudScreenController;
import de.domenic.battleships.scenario.AbstractScenario;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Domenic
 */
public abstract class AbstractGame extends AbstractAppState implements GameEventListener {

    // 2 Spieler
    // Ein Spieler hat ein eigenes Spielfeld, mit Grid, Schiffen usw.
    protected Player playerOne = new Player(), playerTwo = new Player();
    protected Player activePlayer = playerOne;

    private boolean loading; // tells you if game is loading / initializing
    private int loadingCounter = 0;

    // Sound, Grafik, Input etc. wird in AppStates ausgelagert von denen es nur je eine Instanz gibt
    protected VisualAppState visualAppState;
    protected ShipPlacementManager shipPlacementManager;
    protected InputAppState inputState;
    protected CameraControl cameraControl;
    protected AbstractScenario scenario;
    protected SpecialEffectsManager effectManager;
    protected SoundAppState soundAppState;
    protected NiftyAppState niftyAppState;

    private AppStateManager stateManager;
    private SimpleApplication app;

    private final GameSettings settings;

    public AbstractGame(GameSettings settings) {
        this.settings = settings;
    }

    public GameSettings getSettings() {
        return settings;
    }

    protected State gameState = State.PLACING_SHIPS;

    @Override
    public void onStateChanged(State newState) {
        System.out.println("New state: " + newState);
        this.gameState = newState;      
    }

    public State getGameState() {
        return gameState;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.stateManager = stateManager;
        this.app = (SimpleApplication) app;
        this.niftyAppState = stateManager.getState(NiftyAppState.class);
        
        // set default names
        playerOne.setName("Player 1");
        playerTwo.setName("Player 2");
        
        loading = true;
        loadingCounter = 0;

        super.initialize(stateManager, app);
    }
    
    private void setHudStateText(String state) {
        niftyAppState.getScreenController(HudScreenController.class).setState(state);
    }

    @Override
    public void onPlayerTurn(Player nextPlayer) {
        this.activePlayer = nextPlayer;
        setHudStateText("It's " + nextPlayer.getName() + " turn");
    }

    @Override
    public void onMiss(Player attackedPlayer, Position pos) {
        // play a "miss" sound and add a simple particle effect
        soundAppState.playSoundEffect(SoundAppState.SoundEffect.Miss, null);
        attackedPlayer.getGameField().getGrid().setTileAt(pos, Tile.Green);
        
        // little particle effect
        effectManager.addEffect(attackedPlayer, pos, SpecialEffectsManager.Effect.WaterSplash);
    }

    @Override
    public void onShipPlaced(Player player, Ship placedShip) {
        visualAppState.addShipModel(player, placedShip);
        shipPlacementManager.resetLastSelectedZones(player);
        if (!settings.isAutoSet()) {
            nextShipForPlayer(activePlayer);
        }
    }
    
    protected boolean isActive(Player player) {
        return player == activePlayer;
    }

    /**
     * TODO !!!!
     *
     * @param attackedPlayer
     * @param hitPosition
     */
    @Override
    public void onShipHit(Player attackedPlayer, Position hitPosition) {
        attackedPlayer.getGameField().getGrid().setTileAt(hitPosition, Tile.Read);
        // play sound ...
        soundAppState.playSoundEffect(SoundAppState.SoundEffect.HitShip, null);
        // emit particle effect ... (fire)
        effectManager.addEffect(attackedPlayer, hitPosition, SpecialEffectsManager.Effect.Fire);
        
        setHudStateText("It's " + attackedPlayer.getName() + " turn");
    }

    @Override
    public void onGameStart(Player firstPlayer) {
        activePlayer = firstPlayer;
        cameraControl.setOnCyclePath(true);
        
       // refresh hud state text
       setHudStateText("It's " + firstPlayer.getName() + " turn");
    }

    @Override
    public void onAttack(Player attackedPlayer, Position attackedPosition) {
        // play some effects, sounds ...
        soundAppState.playSoundEffect(SoundAppState.SoundEffect.Shoot, null);

        if (settings.isShootEffectsEnabled()) {
            
            setHudStateText("Waiting for reaction");
            
            Player attackingPlayer = getOpponent(attackedPlayer);

            if (visualAppState.getVisualGameFieldByPlayer(attackingPlayer).isVisible()) { // direct shoot
               
                Ship randomShip;
                do {
                    int shipNr = (int) (Math.random() * attackingPlayer.getShips().size());
                    randomShip = attackingPlayer.getShips().get(shipNr);
                } while (randomShip.isDestroyed());
                
                Position attackPos = randomShip.getPositions()[0];

                Vector3f startLoc = visualAppState.convertPositionToWorldLocation(attackingPlayer, attackPos);
                Vector3f endLoc = visualAppState.convertPositionToWorldLocation(attackedPlayer, attackedPosition);

                effectManager.addEffect(attackingPlayer, attackPos, SpecialEffectsManager.Effect.Smoke);
                
                scenario.showAttackAnimation(startLoc, endLoc, true);
            } else {
                Vector3f start = visualAppState.getVisualGameFieldByPlayer(attackingPlayer).getGameFieldNode().getWorldTranslation().add(0, 0, GameUtils.GAME_FIELD_LENGTH / 2);
                if (attackingPlayer == playerTwo) {
                    start.addLocal(GameUtils.GAME_FIELD_LENGTH, 0, 0);
                }
                Vector3f end = visualAppState.convertPositionToWorldLocation(attackedPlayer, attackedPosition);                                
                scenario.showAttackAnimation(start, end, false);
            }
        }
    }

    @Override
    public void onGameDecided(Player playerWhoHasWon) {
        // refrsh gui
        setHudStateText(playerWhoHasWon.getName() + " has won!");
        
        // change cam mode
        Vector3f center = visualAppState.getVisualGameFieldByPlayer(playerWhoHasWon).getGameFieldNode().getWorldTranslation().add(GameUtils.GAME_FIELD_LENGTH/2, 0, GameUtils.GAME_FIELD_LENGTH/2);
        Vector3f position = new Vector3f(GameUtils.GAME_FIELD_LENGTH, 4, -5);
        cameraControl.setFixedCamOrientation(position, center);
    }
    
    

    /**
     * TODO !!!!
     *
     * @param attackedPlayer
     * @param destroyedShip
     */
    @Override
    public void onShipDestroyed(Player attackedPlayer, Ship destroyedShip) {
        
        // play particle effect ...
        for (Position pos : destroyedShip.getPositions()) {
            effectManager.addEffect(attackedPlayer, pos, SpecialEffectsManager.Effect.Explosion);
        }
        
        // change or add destroyed ship model 
        
        GameUtils.setHintTilesForDestroyedShip(attackedPlayer.getGameField(), destroyedShip);
        
        // play sound ...
        soundAppState.playSoundEffect(SoundAppState.SoundEffect.ShipExplosion, null);
        
        // stop fire effects 
        for (Position pos : destroyedShip.getPositions()) {
            effectManager.removeEffects(attackedPlayer, pos, SpecialEffectsManager.Effect.Fire);
        }
    }

    public void onLeftClick() {
        if (gameState == State.PLACING_SHIPS) {
            if (activePlayer != null && shipPlacementManager.getShip() != null) {
                Position pos = visualAppState.getAimedPosition(activePlayer);
                Direction dir = shipPlacementManager.getPlacementDirection();
                if (pos != null && dir != null) {
                    requestPlacing(shipPlacementManager.getShip(), pos, dir);
                }
            }
        } else if (gameState == State.GAME_RUNNING) {
            if (inputState.getInputManager().isCursorVisible()) {
                Position pos = visualAppState.getAimedPosition(getOpponent(activePlayer));
                if (pos != null) {
                    requestShoot(pos);
                }
            }

        }
    }

    protected Player getOpponent(Player p) {
        return p == playerOne ? playerTwo : playerOne;
    }

    protected abstract void requestPlacing(Ship shipToPlace, Position placePos, Direction dir);

    protected abstract void requestShoot(Position attackedPosition);

    public void onRightClick() {
        if (gameState == State.PLACING_SHIPS) {
            shipPlacementManager.changePlacementDirection();
        }
    }

    @Override
    public void update(float tpf) {

        //_________________________________________________
        //      HERE THE GAME IS LOADED STEP BY STEP
        if (loading) {

            switch (loadingCounter) {
                case 0:
                    break;
                case 1:
                    visualAppState = new VisualAppState();
                    stateManager.attach(visualAppState);
                    break;

                case 2:
                    shipPlacementManager = new ShipPlacementManager();
                    stateManager.attach(shipPlacementManager);
                    break;

                case 3:
                    inputState = new InputAppState();
                    stateManager.attach(inputState);
                    break;

                case 4:
                    initInput();
                    break;

                case 5:
                    visualAppState.addGameFieldForPlayer(playerOne);
                    visualAppState.addGameFieldForPlayer(playerTwo);
                    System.out.println("wda");
                    break;
                case 6:
                    cameraControl = new CameraControl();
                    stateManager.attach(cameraControl);
                    cameraControl.setFocusPoint(new Vector3f(10, 0, 5f));
                    cameraControl.setHeightOffset(10);
                    cameraControl.setRadius(15);
                    cameraControl.setOnCyclePath(true);
                    cameraControl.setSmoothMotions(true);
                    break;
                case 7:
                    try {
                        this.scenario = settings.getScenario().getScenario().newInstance();
                        this.stateManager.attach(scenario);
                    } catch (InstantiationException | IllegalAccessException ex) {
                        Logger.getLogger(AbstractGame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                case 8:
                    effectManager = new SpecialEffectsManager();
                    stateManager.attach(effectManager);
                    break;
                case 9:
                    soundAppState = new SoundAppState();
                    stateManager.attach(soundAppState);
                    break;
                case 10:
                    soundAppState.playMusicTrack(SoundAppState.Music.Homelander);
                    break;
                default:
                    loading = false; // loading done
                    onFinishedLoadingGame();
                    break;
            }

            loadingCounter++;
            return;
        }
        // ______________________________________________________
        if (null != gameState)
//            ____________________________________________
//                  HANDLING OF INGAME STATES
        switch (gameState) {
            case WAITING:
                break;         
            case PLACING_SHIPS:
                break;
            case GAME_RUNNING:                
                break;
            default:
                break;
        }
    }

    public abstract void onFinishedLoadingGame();

    
    protected void letPlayerPlaceShips(Player player) {
        if (getSettings().isAutoSet()) {
            return;
        }
        setHudStateText("Place ships");
        activePlayer = player;      
        nextShipForPlayer(player);
        enterCameraPlacingMode(player);        
    }
    
    protected void nextShipForPlayer(Player player) {  
        Ship ship;
        if ((ship = player.getNextShip()) != null) {
            shipPlacementManager.setPlacingPlayerAndShip(player, ship);
        } else {
            shipPlacementManager.reset();
        }
    }

    @Override
    public void onPlayerFinishedPlacing(Player finishedPlayer) {
        shipPlacementManager.resetLastSelectedZones(finishedPlayer);
        shipPlacementManager.reset();
    }

    protected void enterCameraPlacingMode(Player player) {
        // place cam so that placing is easier
        VisualGameField gameField = visualAppState.getVisualGameFieldByPlayer(player);
        Vector3f fieldLoc = gameField.getGameFieldNode().getWorldTranslation();
        Vector3f loc = new Vector3f(fieldLoc.x + GameUtils.GAME_FIELD_LENGTH / 2, 8, fieldLoc.z + 12);
        Vector3f dir = new Vector3f(fieldLoc.x + GameUtils.GAME_FIELD_LENGTH / 2, 0, fieldLoc.z + GameUtils.GAME_FIELD_LENGTH / 2);
        cameraControl.setFixedCamOrientation(loc, dir);
    }
    
    protected void enterTopDownPerspective() {        
        float height = 15;
        Vector3f location = visualAppState.getVisualGameFieldByPlayer(playerOne).getGameFieldNode().getWorldTranslation().add(GameUtils.GAME_FIELD_LENGTH, height, GameUtils.GAME_FIELD_LENGTH/2);
        Vector3f lookAtPos = location.add(0, -height, 0);
        cameraControl.setFixedCamOrientation(location, lookAtPos);         
        cameraControl.reset();
    }

    private void initInput() {
        app.getInputManager().addListener((ActionListener) (String name, boolean isPressed, float tpf) -> {
            if (!isPressed) {
                return;
            }
            switch (name) {
                case InputAppState.LEFT_CLICK:
                    onLeftClick();
                    break;
                case InputAppState.RIGHT_CLICK:
                    onRightClick();
                    break;
                case InputAppState.SPACE:
                    if (gameState == State.GAME_RUNNING) {
                        if (cameraControl.isOnCyclePath()) {
                            enterTopDownPerspective();
                        } else {
                            cameraControl.setOnCyclePath(true);
                        }
                    }   break;
                default:
                    break;
            }
        }, InputAppState.LEFT_CLICK, InputAppState.RIGHT_CLICK, InputAppState.SPACE);
    }

    @Override
    public void cleanup() {        
        stateManager.detach(visualAppState);
        stateManager.detach(shipPlacementManager);
        stateManager.detach(inputState);
        stateManager.detach(scenario);
        stateManager.detach(cameraControl);
        stateManager.detach(effectManager);
        stateManager.detach(soundAppState);
        super.cleanup();
    }
}
