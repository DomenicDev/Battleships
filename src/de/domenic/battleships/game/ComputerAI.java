package de.domenic.battleships.game;

import java.util.ArrayList;

/**
 *
 * @author Domenic
 */
public class ComputerAI implements GridEventListener {

    private ArrayList<Position> lastHitPos = new ArrayList<>();
    private Position lastShootPos;
    private Grid grid; // grid of human player

    public ComputerAI(Grid grid) {
        this.grid = grid;
        this.grid.addGridListener(this);

        lastShootPos = new Position(-1, -1);
    }

    public Position attack() {
        if (lastHitPos.isEmpty()) {
            do {
                GameUtils.getRandomPosition(lastShootPos);
            } while (grid.getTileAt(lastShootPos) != Grid.Tile.Empty);
        } else if (lastHitPos.size() == 1) {
            do {
                Direction dir = GameUtils.getRandomDirection();
                lastShootPos.setX(lastHitPos.get(0).getX() + dir.getXDiff());
                lastShootPos.setY(lastHitPos.get(0).getY() + dir.getYDiff());
            } while (!GameUtils.isValidPosition(lastShootPos) || grid.getTileAt(lastShootPos) != Grid.Tile.Empty);
        } else if (lastHitPos.size() >= 2) {
            // we have at least 2 positions!
            Position pos1 = lastHitPos.get(0);
            Position pos2 = lastHitPos.get(1);
            
            Direction dir = GameUtils.getDirection(pos1, pos2);
            if (getNextAttackPos(dir) == null) {
                Direction oppDir = GameUtils.getOppositeDirection(dir);
                getNextAttackPos(oppDir);
            }                
        }
        return new Position(lastShootPos.getX(), lastShootPos.getY());
    }

    /**
     * Only used when a ship was hitted twice (or more).
     * @param dir
     * @return 
     */
    private Position getNextAttackPos(Direction dir) {
        for (Position pos : lastHitPos) {
            lastShootPos.setX(pos.getX() + dir.getXDiff());
            lastShootPos.setY(pos.getY() + dir.getYDiff());
            if (GameUtils.isValidPosition(lastShootPos) && grid.getTileAt(lastShootPos) == Grid.Tile.Empty) {
                return lastShootPos;
            }
        }
        return null;
    }

    public void onShipDestroyed() {
        lastHitPos.clear();
    }

    public Position getLastShootPos() {
        return lastShootPos;
    }

    @Override
    public void onTileChange(Grid grid, Position pos, Grid.Tile newTile) {

        // if we hit something we refresh lastHitPos
        if (newTile == Grid.Tile.Read) {
            if (!lastHitPos.contains(pos)) {
                lastHitPos.add(pos);
            }
        }
    }

}
