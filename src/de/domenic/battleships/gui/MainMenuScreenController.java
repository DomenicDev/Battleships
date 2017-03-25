package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;

/**
 *
 * @author Domenic
 */
public class MainMenuScreenController extends AbstractScreenController {
    
    public MainMenuScreenController(GameAppState gameState) {
        super(gameState);
    }
    
    public void startGame() {
        nifty.gotoScreen("singleplayerScreen");
    }
    
    public void hostNetworkGame() {
        gameState.setupServerAndClient();
        nifty.gotoScreen(Screen.HostGameScreen.getIdName());
    }
    
    public void connectToGame() {
        nifty.gotoScreen("connectionScreen");
        gameState.setupClient();
    }
    
    public void openCredits() {
        
    }
    
    public void quitGame() {
        gameState.quit();
    }
}
