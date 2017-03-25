package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 *
 * @author Domenic
 */
public class InputAppState extends AbstractAppState {
    
    private InputManager inputManager;
    
    public static final String LEFT_CLICK = "LeftClick";
    public static final String RIGHT_CLICK = "RightClick";
    public static final String SPACE ="Space";
    

    public InputAppState() {
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.inputManager = app.getInputManager();
        this.inputManager.addMapping(LEFT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        this.inputManager.addMapping(RIGHT_CLICK, new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        this.inputManager.addMapping(SPACE, new KeyTrigger(KeyInput.KEY_SPACE));
    }  

    @Override
    public void cleanup() {        
        inputManager.deleteMapping(LEFT_CLICK);
        inputManager.deleteMapping(RIGHT_CLICK);
        inputManager.deleteMapping(SPACE);
        super.cleanup(); 
    }
}
