package de.domenic.battleships.scenario;

/**
 *
 * @author Domenic
 */
public enum Scenario {
    
    Sea("Sea", SeaScenario.class);
    
    private Scenario(String name, Class<? extends AbstractScenario> scenario) {
        this.name = name;
        this.scenario = scenario;
    }
    
    private final String name;
    private final Class<? extends AbstractScenario> scenario;

    public String getName() {
        return name;
    }

    public Class<? extends AbstractScenario> getScenario() {
        return scenario;
    }
    
}
