package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.scenario.Scenario;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Domenic
 */
public class HostGameScreenController extends AbstractScreenController { 
    
    private RadioButton sea; // scenarios
    private CheckBox shootEffects;
    private CheckBox autoSet;
    private Label enemy;

    private String enemyName;
    
    public HostGameScreenController(GameAppState gameState) {
        super(gameState);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        super.bind(nifty, screen); 
        sea = screen.findNiftyControl("sea", RadioButton.class);
        shootEffects = screen.findNiftyControl("shootEffectsCheckBox", CheckBox.class);
        autoSet = screen.findNiftyControl("autoSetCheckBox", CheckBox.class);
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
    
    
    
     public void startGame() {
        Scenario s = Scenario.Sea; // for now
        boolean shootEffectsEnabled = shootEffects.isChecked();
        boolean autoSetEnabled = autoSet.isChecked();
        
        GameSettings settings = new GameSettings();
        settings.setShootEffectsEnabled(shootEffectsEnabled);
        settings.setScenario(s);
        settings.setAutoSet(autoSetEnabled);
        
        gameState.startNetworkGame(settings);
    }
    
}
