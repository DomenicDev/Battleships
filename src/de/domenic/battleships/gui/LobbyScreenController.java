package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 *
 * @author Domenic
 */
public class LobbyScreenController extends AbstractScreenController {
    
    private Label enemy;
    
    private String enemyName;
    
    public LobbyScreenController(GameAppState gameState) {
        super(gameState);
    }
    
     @Override
    public void bind(Nifty nifty, de.lessvoid.nifty.screen.Screen screen) {
        super.bind(nifty, screen); 
        enemy = screen.findNiftyControl("networkPlayerLabel", Label.class);
        if (enemyName != null) {
            setEnemyName(enemyName);
        }
    }
    
    public void setEnemyName(String name) {    
        if (enemy != null) {
            enemy.getElement().getRenderer(TextRenderer.class).setText(name);      
        } 
        enemyName = name;          
    }
    
}
