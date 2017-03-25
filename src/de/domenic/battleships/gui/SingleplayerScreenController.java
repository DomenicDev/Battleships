package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.scenario.Scenario;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.CheckBox;
import de.lessvoid.nifty.controls.RadioButton;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Domenic
 */
public class SingleplayerScreenController extends AbstractScreenController {
    
    private RadioButton human;
    private RadioButton sea; // scenarios
    private CheckBox shootEffects;
    private CheckBox autoSet;
    
    public SingleplayerScreenController(GameAppState gameState) {
        super(gameState);
    }
    
    public void startGame() {
        boolean humanPlayer = human.isActivated();
        Scenario s = Scenario.Sea; // for now
        boolean shootEffectsEnabled = shootEffects.isChecked();
        
        GameSettings settings = new GameSettings();
        settings.setShootEffectsEnabled(shootEffectsEnabled);
        settings.setAutoSet(autoSet.isChecked());
        settings.setScenario(s);
        
        gameState.startGame(settings, humanPlayer);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        super.bind(nifty, screen); 
        human = screen.findNiftyControl("human", RadioButton.class);
        sea = screen.findNiftyControl("sea", RadioButton.class);
        shootEffects = screen.findNiftyControl("shootEffectsCheckBox", CheckBox.class);
        autoSet = screen.findNiftyControl("autoSetCheckBox", CheckBox.class);
    }
    
    
    
}
