package de.domenic.battleships.game;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 * This is just a helper class which provides methods and field which are used
 * all around the application.
 *
 * @author Domenic
 */
public class GameUtils {

    // size of the game field in one axis
    public static final int GAME_FIELD_LENGTH = 10;

    // number of ships for each type
    public static final int AMOUNT_OF_FOUR_SPACE_SHIPS = 1;
    public static final int AMOUNT_OF_THREE_SPACE_SHIPS = 2;
    public static final int AMOUNT_OF_TWO_SPACE_SHIPS = 2;
    public static final int AMOUNT_OF_ONE_SPACE_SHIPS = 2;

    public static final float FLIGHT_DURATION = 2; // for attack animations

    // total number of ships
    public static final int AMOUNT_OF_SHIPS = AMOUNT_OF_FOUR_SPACE_SHIPS + AMOUNT_OF_THREE_SPACE_SHIPS + AMOUNT_OF_TWO_SPACE_SHIPS + AMOUNT_OF_ONE_SPACE_SHIPS;

    public static Position convertToPosition(Vector3f vector, boolean round) {
        if (round) {
            return new Position((int) Math.round(vector.getX()), (int) Math.round(vector.getZ()));
        } else {
            return new Position((int) vector.getX(), (int) vector.getZ());
        }
    }

    public static Vector3f convertToVector3f(Position position) {
        return new Vector3f(position.getX(), 0, position.getY());
    }

    public static Vector3f getCenter(Position[] positions) {
        return getCenter(positions, new Vector3f());
    }

    public static Vector3f getCenter(Position[] positions, Vector3f storeVector) {
        float x = 0;
        float y = 0;

        for (Position pos : positions) {
            x += pos.getX();
            y += pos.getY();
        }
        storeVector.set((float) x / positions.length, 0, (float) y / positions.length);
        return storeVector;
    }

    public static Quaternion getRotationFromDirection(Direction dir) {
        float[] angles = new float[3];

        if (null != dir) // this needs to be checked
        {
            switch (dir) {
                case Down:
                    angles[1] = 0;
                    break;
                case Up:
                    angles[1] = -180 * FastMath.DEG_TO_RAD;
                    break;
                case Left:
                    angles[1] = -90 * FastMath.DEG_TO_RAD;
                    break;
                case Right:
                    angles[1] = 90 * FastMath.DEG_TO_RAD;
                    break;
                default:
                    break;
            }
        }
        return new Quaternion(angles);
    }

    /**
     * Checks whether the given position is within the game field range.
     * @param pos
     * @return true if the position is within the game field.
     */
    public static boolean isValidPosition(Position pos) {
        if (pos == null) {
            return false;
        }
        int x = pos.getX();
        int y = pos.getY();

        return (x >= 0 && x < GameUtils.GAME_FIELD_LENGTH && y >= 0 && y < GameUtils.GAME_FIELD_LENGTH);
    }

    public static Ship[] createShips() {
        Ship[] ships = new Ship[GameUtils.AMOUNT_OF_SHIPS];
        int shipCounter = 0;

        // init all ships
        for (int i = 0; i < GameUtils.AMOUNT_OF_FOUR_SPACE_SHIPS; i++) {
            createShip(ships, shipCounter, 4);
            shipCounter++;
        }
        for (int i = 0; i < GameUtils.AMOUNT_OF_THREE_SPACE_SHIPS; i++) {
            createShip(ships, shipCounter, 3);
            shipCounter++;
        }
        for (int i = 0; i < GameUtils.AMOUNT_OF_TWO_SPACE_SHIPS; i++) {
            createShip(ships, shipCounter, 2);
            shipCounter++;
        }
        for (int i = 0; i < GameUtils.AMOUNT_OF_ONE_SPACE_SHIPS; i++) {
            createShip(ships, shipCounter, 1);
            shipCounter++;
        }
        return ships;
    }

    private static void createShip(Ship[] ships, int index, int length) {
        ships[index] = new Ship();
        ships[index].setLength(length);
    }

    /**
     * Check whether the given ship can be placed at the intended position. If
     * it actually can the positions for this ship will be returned. Note: The
     * ships position isn't set by this method.
     *
     * @param player the player the ships belongs to
     * @param ship the ship which shall be checked for placing
     * @param topPos the "aimed" position of the ship
     * @param dir the direction of the ship
     * @return null if it can not be placed, otherwise the locations for that
     * ship
     */
    public static Position[] canShipBePlaced(Player player, Ship ship, Position topPos, Direction dir) {
        if (isValidPosition(topPos)) {
            int x = topPos.getX();
            int y = topPos.getY();

            Position[] locsOfShip = new Position[ship.getLength()];
            int index = 0;

            // check for every ship segment (maybe this can be optimized a little bit)
            for (int i = 0; i < ship.getLength(); i++) {
                Position pos = new Position(x + i * dir.getXDiff(), y + i * dir.getYDiff());
                if (!isValidPosition(pos)) {
                    return null;
                }

                // check potential intersection with other ships
                for (Ship otherShip : player.getShips()) {
                    if (otherShip == null || otherShip == ship) {
                        continue;
                    }
                    Position[] positionsFromOtherShip = otherShip.getPositions();
                    if (positionsFromOtherShip == null) {
                        continue;
                    }
                    // check whether this ship would directly intersect with another one
                    for (Position comparePos : positionsFromOtherShip) {
                        if (pos.equals(comparePos)) {
                            return null; // position intersects with another ship -> not allowed
                        }
                    }

                    // check reserved positions also
                    for (Position reservedPos : getSurroundedTiles(pos)) {
                        for (Position posToCompare : otherShip.getPositions()) {
                            if (reservedPos.equals(posToCompare)) {
                                return null; // res. position intersects with ship -> not allowed
                            }
                        }
                    }
                }
                locsOfShip[index] = pos;
                index++;
            }
            return locsOfShip;
        }
        return null;
    }

    /**
     * Will mark all tiles around the destroyed ship as "hint", because there can't be any ships be placed.
     * @param gameField
     * @param destroyedShip 
     */
    public static void setHintTilesForDestroyedShip(GameField gameField, Ship destroyedShip) {
        Position[] positions = destroyedShip.getPositions();
        for (Position pos : positions) {
            Position[] tilesAround = getSurroundedTiles(pos);
            for (Position reservedPos : tilesAround) {
                if (gameField.getGrid().getTileAt(reservedPos) == Grid.Tile.Empty) {
                    gameField.getGrid().setTileAt(reservedPos, Grid.Tile.LightBlue);
                }
            }

        }
    }

    private static Position[] getSurroundedTiles(Position pos) {
        ArrayList<Position> tilePositions = new ArrayList<>();

        for (Direction d : Direction.values()) {
            for (Direction d2 : Direction.values()) {

                int x_check = pos.getX() + d.getXDiff();
                int y_check = pos.getY() + d.getYDiff();

                if (d != d2) {
                    x_check += d2.getXDiff();
                    y_check += d2.getYDiff();
                }
                Position reservedPos = new Position(x_check, y_check);
                if (isValidPosition(reservedPos)) {
                    tilePositions.add(reservedPos);
                }
            }
        }
        return tilePositions.toArray(new Position[tilePositions.size()]);
    }
    
    public static Position getRandomPosition(Position posToStore) {
        if (posToStore == null) {
            posToStore = new Position();
        }
        int x = (int) (Math.random() * GAME_FIELD_LENGTH);
        int y = (int) (Math.random() * GAME_FIELD_LENGTH);
        
        posToStore.setX(x);
        posToStore.setY(y);
        
        return posToStore;
    }
    
    public static Direction getRandomDirection() {        
        int x = (int) (Math.random() * 4);
        return Direction.values()[x];        
    }
    
    /**
     * 
     * @param pos1
     * @param pos2
     * @return 
     */
    public static Direction getDirection(Position pos1, Position pos2) {
        int x = pos2.getX() - pos1.getX();
        int y = pos2.getY() - pos1.getY();
        
        for (Direction dir : Direction.values()) {
            if (dir.getXDiff() == x && dir.getYDiff() == y) {
                return dir;
            }
        }
        return null;
    }
    
    public static Direction getOppositeDirection(Direction dir) {
        switch (dir) {
            case Down: return Direction.Up;
            case Up: return Direction.Down;
            case Left : return Direction.Right;
            case Right: return Direction.Left;
        }
        return null;
    }
    
}
