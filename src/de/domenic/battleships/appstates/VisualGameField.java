package de.domenic.battleships.appstates;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author Domenic
 */
public class VisualGameField {

    private Node gameFieldNode;
    private Node tileNode;
    private Node[][] tiles;
    private boolean visible = true;

    // because we need to add ships dynamically
    private HashMap<Ship, Node> ships;

    public VisualGameField() {
        gameFieldNode = new Node("GameFieldOfPlayer");
        tileNode = new Node("TileNode");
        gameFieldNode.attachChild(tileNode);
        tiles = new Node[GameUtils.GAME_FIELD_LENGTH][GameUtils.GAME_FIELD_LENGTH];

        ships = new HashMap<>();
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    public Node getGameFieldNode() {
        return gameFieldNode;
    }

    public Node[][] getTiles() {
        return tiles;
    }

    public Node getTileNode() {
        return tileNode;
    }

    public Collection<Node> getShips() {
        return ships.values();
    }

    public void addShip(Ship s, Node model) {
        if (s == null || model == null) {
            return;
        }
        this.ships.put(s, model);
        model.setLocalTranslation(getCenter(s));
        model.setLocalRotation(GameUtils.getRotationFromDirection(s.getDirection()));
        gameFieldNode.attachChild(model);
    }

    private Vector3f getCenter(Ship ship) {
        Position[] pos = ship.getPositions();
        Position p1 = pos[0];
        Position p2 = pos[pos.length - 1];

        Vector3f center = new Vector3f();
        center.setX((float) (p1.getX() + p2.getX()) / 2 /*+(float) VisualAppState.QUAD_LENGTH / 2*/);
        center.setZ((float) (p1.getY() + p2.getY()) / 2  /*+(float) VisualAppState.QUAD_LENGTH / 2*/);
        return center;
    }

   
    

    public Node getShipModel(Ship ship) {
        for (Ship s : ships.keySet()) {
            if (s.equals(ship)) {
                return ships.get(s);
            }
        }
        return null;
    }

    public void removeAllShipModels() {        
        Ship[] array = ships.keySet().toArray(new Ship[ships.size()]);
        for (Ship ship : array) {
            removeShip(ship);
        }
    }

    /**
     * Remove the ship model for this game object from scene and from the local
     * list.
     *
     * @param s ship to remove
     */
    public void removeShip(Ship s) {
        Node model = ships.get(s);
        if (model != null) {
            model.removeFromParent();
            ships.remove(s);
        }
    }

}
