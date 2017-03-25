package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Domenic
 */
public abstract class AbstractScreenController implements ScreenController {
    
    protected Nifty nifty;
    protected GameAppState gameState;
    
    public AbstractScreenController(GameAppState gameState) {
        this.gameState = gameState;                
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    @Override
    public void onStartScreen() {
        
    }

    @Override
    public void onEndScreen() {
        
    }
    
}
