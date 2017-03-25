package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.GameUtils;
import de.domenic.battleships.game.Grid;
import de.domenic.battleships.game.Player;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;

/**
 *
 * @author Domenic
 */
public class ShipPlacementManager extends AbstractAppState {

    private VisualAppState visualAppState;

    private Player player;
    private Ship ship;
    private Node shipModel;

    private Vector3f centerVector = new Vector3f();

    private Position lastLookingPositionWhenPlacing;
    private Position[] selectedFieldsWhilePlacing;

    private Direction shipPlacementDir = Direction.Down;
    private Direction lastLookingDir = shipPlacementDir;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.visualAppState = stateManager.getState(VisualAppState.class);
    }

    public void setPlacingPlayerAndShip(Player placingPlayer, Ship shipToPlace) {
        if (player != null) {
            resetLastSelectedZones(player);
        }
        reset();
        this.ship = shipToPlace;
        this.player = placingPlayer;
        this.shipModel = visualAppState.getShipModel(shipToPlace);
        this.shipModel.setCullHint(Spatial.CullHint.Always);
        this.visualAppState.getVisualGameFieldByPlayer(player).getGameFieldNode().attachChild(shipModel);
    }
    
    public void resetLastSelectedZones(Player player) {
        // set the texture of the last selected zones back to "Empty"
        if (selectedFieldsWhilePlacing != null) {
            for (Position pos : selectedFieldsWhilePlacing) {
                player.getGameField().getGrid().setTileAt(pos, Grid.Tile.Empty);
            }
        }
    }

    public Direction getPlacementDirection() {
        return shipPlacementDir;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }

    public void reset() {        
        this.player = null;
        this.ship = null;
        if (this.shipModel != null) {
            this.shipModel.removeFromParent();
            this.shipModel = null;
        }
    }

    public Ship getShip() {
        return ship;
    }

    public Player getPlayer() {
        return player;
    }

    public Direction changePlacementDirection() {
        if (null != shipPlacementDir) {
            switch (shipPlacementDir) {
                case Down:
                    shipPlacementDir = Direction.Left;
                    break;
                case Left:
                    shipPlacementDir = Direction.Up;
                    break;
                case Up:
                    shipPlacementDir = Direction.Right;
                    break;
                case Right:
                    shipPlacementDir = Direction.Down;
                    break;
                default:
                    break;
            }
        }
        return shipPlacementDir;
    }

    @Override
    public void update(float tpf) {
        if (player == null || ship == null || shipModel == null) {
            return;
        }

        Position lookPos = visualAppState.getAimedPosition(player);
        if (lookPos != null) {
            if (lookPos.equals(lastLookingPositionWhenPlacing) && shipPlacementDir == lastLookingDir) {
                return;
            }
            lastLookingPositionWhenPlacing = lookPos;
            resetLastSelectedZones(player);

            // show new zones as selected
            Position[] pos = GameUtils.canShipBePlaced(player, ship, lookPos, shipPlacementDir);

            selectedFieldsWhilePlacing = pos;
            lastLookingDir = shipPlacementDir;
            if (pos != null) {
                shipModel.setCullHint(Spatial.CullHint.Inherit);
                for (Position p : pos) {
                    player.getGameField().getGrid().setTileAt(p, Grid.Tile.Place);
                }
                centerVector = GameUtils.getCenter(pos, centerVector);
                shipModel.setLocalTranslation(centerVector);
                shipModel.setLocalRotation(GameUtils.getRotationFromDirection(lastLookingDir));
            } else {
                shipModel.setCullHint(Spatial.CullHint.Always);
            }
        } else {
            resetLastSelectedZones(player);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
    }

}
