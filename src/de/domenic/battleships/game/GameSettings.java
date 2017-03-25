package de.domenic.battleships.game;

import com.jme3.network.serializing.Serializable;
import de.domenic.battleships.scenario.Scenario;

/**
 *
 * @author Domenic
 */
@Serializable
public class GameSettings {
    
    private Scenario scenario;    
    private boolean shootEffectsEnabled;
    private boolean autoSet;

    public void setAutoSet(boolean autoSet) {
        this.autoSet = autoSet;
    }

    public boolean isAutoSet() {
        return autoSet;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;
    }

    public void setShootEffectsEnabled(boolean shootEffectsEnabled) {
        this.shootEffectsEnabled = shootEffectsEnabled;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public boolean isShootEffectsEnabled() {
        return shootEffectsEnabled;
    }
    
}
