package de.domenic.battleships.game;

/**
 *
 * @author Domenic
 */
public class GameField {

    private Grid grid;

    // used to place ships
//    private Place[][] gameField;

//    public enum Place {
//
//        /**
//         * A ship is at the position
//         */
//        Ship,
//        /**
//         * There cannot be placed a ship because it's to close to another
//         */
//        Reserved,
//        /**
//         * Here it can be placed
//         */
//        Free;
//    }

    public GameField() {
    //    gameField = new Place[GameUtils.GAME_FIELD_LENGTH][GameUtils.GAME_FIELD_LENGTH];
        grid = new Grid();

        // init game field with free zones
//        for (int y = 0; y < GameUtils.GAME_FIELD_LENGTH; y++) {
//            for (int x = 0; x < GameUtils.GAME_FIELD_LENGTH; x++) {
//                gameField[y][x] = Place.Free;
//            }
//        }
    }

    public Grid getGrid() {
        return this.grid;
    }
//
//    public void setPlace(Place p, Position pos) {
//        if (GameUtils.isValidPosition(pos)) {
//            gameField[pos.getY()][pos.getX()] = p;
//        }
//    }
//
//    public Place getElementAt(Position pos) {
//        if (GameUtils.isValidPosition(pos)) {
//            return gameField[pos.getY()][pos.getX()];
//        }
//        return null;
//    }

  

//
//    /**
//     *
//     * @param ship
//     * @param topPos
//     * @param dir
//     * @return null if it can not be placed, otherwise the locations for that
//     * ship
//     */
//    public Position[] canShipBePlaced(Ship ship, Position topPos, Direction dir) {
//        int x = topPos.getX();
//        int y = topPos.getY();
//
//        if (x >= 0 && x < GameUtils.GAME_FIELD_LENGTH && y >= 0 && y < GameUtils.GAME_FIELD_LENGTH) {
//            Position[] locsOfShip = new Position[ship.getLength()];
//            int counter = 0;
//            for (int i = 0; i < ship.getLength(); i++) {
//                Position pos = new Position(x + i * dir.getXDiff(), y + i * dir.getYDiff());
//                if (!isPositionFree(pos)) {
//                    return null;
//                }
//                locsOfShip[counter] = pos;
//                counter++;
//            }
//            return locsOfShip;
//        }
//        return null;
//    }
//   

//    public Position[] placeShip(Ship ship, Position topPos, Direction dir) {
//        Position[] pos = canShipBePlaced(ship, topPos, dir);
//        if (pos != null) {
//            ship.setPositions(pos);
//            // set places for ships first
//            for (Position p : pos) {
//                gameField[p.getY()][p.getX()] = Place.Ship;
//            }
//            // set reserved fields
//            for (Position p : pos) {
//                for (Direction d : Direction.values()) {
//                    for (Direction d2 : Direction.values()) {
//                        int x = p.getX() + d.getXDiff();
//                        int y = p.getY() + d.getYDiff();
//                        if (d != d2) {
//                            x += d2.getXDiff();
//                            y += d2.getYDiff();
//                        }
//                        Position reservedPos = new Position(x, y);
//
//                        if (isPositionFree(reservedPos)) {
//                            gameField[reservedPos.getY()][reservedPos.getX()] = Place.Reserved;
//                        }
//                    }
//                }
//            }
//            return pos;
//        }
//        return null;
//    }

//    private boolean isPositionFree(Position pos) {
//        int x = pos.getX();
//        int y = pos.getY();
//
//        if (x >= 0 && x < GameUtils.GAME_FIELD_LENGTH && y >= 0 && y < GameUtils.GAME_FIELD_LENGTH) {
//            return gameField[pos.getY()][pos.getX()] == Place.Free;
//        } else {
//            return false;
//        }
//    }
}
