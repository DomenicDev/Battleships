package de.domenic.battleships.game;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Domenic
 */
public class Player {

    private GameField gameField;
    private ArrayList<Ship> ships = new ArrayList<>();
    
    private String name;
    
    private int shipsPlaced = 0;

    /**
     * DON'T FORGET TO set the ship array later !
     */
    public Player() {
        gameField = new GameField();
    }
    
    public void addShip(Ship ship) {
        this.ships.add(ship);
    } 

    public void setShips(Ship[] ships) {
        this.ships.addAll(Arrays.asList(ships));
    }    

    public Ship getNextShip() {
        if (shipsPlaced < ships.size()) {
            shipsPlaced++;
            return ships.get(shipsPlaced-1);
        }
        return null;
    }

    public Ship getShipAt(int index) {
        if (index >= 0 && index < ships.size()) {
            return ships.get(index);
        }
        return null;
    }
    
    public int getNumberOfAliveShips() {
        int number = 0;
        for (Ship s : ships) {
            if (!s.isDestroyed()) {
                number++;
            }
        }
        return number;
    }
    
    public ArrayList<Ship> getShips() {
        return ships;
    }
    
    public GameField getGameField() {
        return gameField;
    }
    
    public boolean hasPlacedAllShips() {
        return ships.stream().noneMatch((ship) -> (ship.getPositions() == null));
    }
    
    public Ship getShip(int id) {
        for (Ship s : ships) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
