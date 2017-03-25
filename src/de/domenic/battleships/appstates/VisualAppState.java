package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import de.domenic.battleships.controls.ShipRotationControl;
import de.domenic.battleships.game.GameField;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Grid;
import de.domenic.battleships.game.GridEventListener;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 *
 * @author Domenic
 */
public class VisualAppState extends AbstractAppState implements GridEventListener {

    private final String EMPTY = "Textures/Tiles/my_zone.png";
    private final String PLACE = "Textures/Tiles/my_place.png";
    private final String HINT = "Textures/Tiles/my_hint.png";
    private final String MISSED = "Textures/Tiles/my_missed.png";
    private final String HIT = "Textures/Tiles/my_hit.png";

    private Texture empty;
    private Texture place;
    private Texture hint;
    private Texture missed;
    private Texture hit;

    private Node tileModel;
    private Node rootNode;

    private AssetManager assetManager;
    private InputManager inputManager;
    private Camera cam;

    private AmbientLight ambient;

    public static final int QUAD_LENGTH = 1;

    private HashMap<Player, VisualGameField> visualGameFields = new HashMap<>();

    public VisualAppState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.rootNode = ((SimpleApplication) app).getRootNode();
        this.cam = app.getCamera();

        tileModel = (Node) assetManager.loadModel("Models/Tile.j3o");

        // init textures
        empty = assetManager.loadTexture(EMPTY);
        place = assetManager.loadTexture(PLACE);
        hint = assetManager.loadTexture(HINT);
        missed = assetManager.loadTexture(MISSED);
        hit = assetManager.loadTexture(HIT);

        // we need to add some light
        /**
         * A white ambient light source.
         */
        ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White);
        rootNode.addLight(ambient);

        super.initialize(stateManager, app);
    }

    public void addGameFieldForPlayer(Player playerToAdd) {
        VisualGameField gameField = new VisualGameField();
        gameField.getGameFieldNode().setLocalTranslation(visualGameFields.size() * GameUtils.GAME_FIELD_LENGTH * QUAD_LENGTH + visualGameFields.size(), 0, 0);
        rootNode.attachChild(gameField.getGameFieldNode());

        Node[][] tiles = gameField.getTiles();
        initGameField(playerToAdd.getGameField(), gameField.getTileNode(), tiles);
        visualGameFields.put(playerToAdd, gameField);

        playerToAdd.getGameField().getGrid().addGridListener(this);
    }

    public Node getRootNode() {
        return rootNode;
    }

    private void initGameField(GameField field, Node node, Node[][] tiles) {
        for (int y = 0; y < GameUtils.GAME_FIELD_LENGTH; y++) {
            for (int x = 0; x < GameUtils.GAME_FIELD_LENGTH; x++) {
                Node tile = getTile(field.getGrid().getTileAt(x, y));
                tile.setLocalTranslation(x, 0, y);
                tiles[y][x] = tile;
                node.attachChild(tile);
            }
        }
    }

    public Position getAimedPosition(Player activePlayer) {
        CollisionResult result = getClosestCollisionFromRayCast(activePlayer);
        if (result != null) {
            VisualGameField gameField = getVisualGameFieldByPlayer(activePlayer);
            Node[][] tiles = gameField.getTiles();
            for (int y = 0; y < GameUtils.GAME_FIELD_LENGTH; y++) {
                for (int x = 0; x < GameUtils.GAME_FIELD_LENGTH; x++) {
                    if (tiles[y][x].getChild(0).equals(result.getGeometry())) {
                        return new Position(x, y);
                    }
                }
            }
        }
        return null;
    }

    private CollisionResult getClosestCollisionFromRayCast(Player player) {       
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        Ray ray = new Ray(click3d, dir);
        CollisionResults results = new CollisionResults();

        Node gameField = getVisualGameFieldByPlayer(player).getTileNode();
        gameField.collideWith(ray, results);

        if (results.size() > 0) {
            return results.getClosestCollision();
        }
        return null;
    }

    public Vector3f convertPositionToWorldLocation(Player player, Position pos) {
        VisualGameField gameField = getVisualGameFieldByPlayer(player);
        return gameField.getTiles()[pos.getY()][pos.getX()].getWorldTranslation();
    }

    /**
     * Get the a clone from the tile model with the right texture of the given
     * tile
     *
     * @param tile
     * @return a node object. The child(0) is the quad geometry
     */
    public Node getTile(Grid.Tile tile) {
        Node clonedTile = (Node) tileModel.clone();
        Geometry geom = (Geometry) clonedTile.getChild(0);
        changeTextureOfTile(geom, tile);
        return clonedTile;
    }

    private Texture getSpecificTexture(Grid.Tile tile) {
        switch (tile) {
            case Empty:
                return empty;
            case Place:
                return place;
            case Green:
                return missed;
            case LightBlue:
                return hint;
            case Read:
                return hit;
            default: return null;
        }
    }
    
    private ColorRGBA getColor(Grid.Tile tile) {
        switch (tile) {
            case Empty:
                return ColorRGBA.Cyan;
            case Place: 
                return ColorRGBA.Orange;
            case Green:
                return ColorRGBA.Green;
            case LightBlue:
                return ColorRGBA.Blue;
            case Read:
                return ColorRGBA.Red;
                default: return null;
        }
    }

    private void changeTextureOfTile(Geometry geom, Grid.Tile newTile) {
        Material mat = geom.getMaterial();
        Texture tex = getSpecificTexture(newTile);
        ColorRGBA color = getColor(newTile);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", color);
        mat.setTexture("AlphaMap", tex);
    }

    @Override
    public void update(float tpf) {
    }

    public VisualGameField getVisualGameFieldByPlayer(Player player) {
        Iterator<Entry<Player, VisualGameField>> it = visualGameFields.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Player, VisualGameField> e = it.next();
            if (e.getKey() == player) {
                return e.getValue();
            }
        }
        return null;
    }

    @Override
    public void onTileChange(Grid grid, Position pos, Grid.Tile newTile) {
        Node[][] tiles = getVisualGameFieldNodeByGrid(grid).getTiles();
        Node tile = tiles[pos.getY()][pos.getX()];
        Geometry geom = (Geometry) tile.getChild(0);
        changeTextureOfTile(geom, newTile);
    }

    private VisualGameField getVisualGameFieldNodeByGrid(Grid grid) {
        Iterator<Entry<Player, VisualGameField>> it = visualGameFields.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Player, VisualGameField> e = it.next();
            if (e.getKey().getGameField().getGrid() == grid) {
                return e.getValue();
            }
        }
        return null;
    }

    public void addShipModel(Player player, Ship ship) {
        VisualGameField gameField = getVisualGameFieldByPlayer(player);
        Node model = getShipModel(ship);
        if (model != null) {
            gameField.addShip(ship, model);
        }
    }
 

    public void destroyShip(Player player, Ship ship) {
        VisualGameField gameField = getVisualGameFieldByPlayer(player);
        gameField.removeShip(ship); // later add destroyed model
    }

    public Node getShipModel(Ship ship) {
        String name = getShipName(ship);
        if (name != null) {
            Node model = (Node) assetManager.loadModel("Models/Ships/Sea/" + name + ".j3o");
            ShipRotationControl rotationControl = new ShipRotationControl();
            rotationControl.setStrength(5);
            model.addControl(rotationControl);
            
            Node shipRootNode = new Node("ShipRootNode");
            shipRootNode.attachChild(model);
            
            return shipRootNode;
        }
        return null;
    }

    private String getShipName(Ship ship) {
        int length = ship.getLength();
        switch (length) {
            case 4:
                return "Carrier";
            case 3:
                return "Battleship";
            case 2:
                return "Cruiser";
            case 1:
                return "Spy";
            default:
                return null;
        }
    }

    public void removeAllShips() {
        for (VisualGameField gameField : visualGameFields.values()) {
            gameField.removeAllShipModels();
        }

    }

    /**
     * TO DO !!! REMOVE ALL NODES
     */
    @Override
    public void cleanup() {
        super.cleanup();
        VisualGameField[] gameFields = visualGameFields.values().toArray(new VisualGameField[visualGameFields.entrySet().size()]);
        for (VisualGameField gameField : gameFields) {
            gameField.getGameFieldNode().removeFromParent();
            gameField.removeAllShipModels();
        }
        visualGameFields.clear();
        rootNode.removeLight(ambient);
        rootNode.detachAllChildren();
    }

}
