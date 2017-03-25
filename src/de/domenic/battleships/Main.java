package de.domenic.battleships;

import de.domenic.battleships.appstates.GameAppState;
import com.jme3.app.SimpleApplication;

public class Main extends SimpleApplication {

    public static void main(String[] args) {
        NetworkUtils.initSerializers();

        Main app = new Main();
        app.start();
    }

    
    @Override    
    public void simpleInitApp() {
        setDisplayFps(false);
        setDisplayStatView(false);
        
        // we use our own cam control
        flyCam.setEnabled(false);    
     
        // we use a cleaner way to stop the game
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        
        // very important for network games
        setPauseOnLostFocus(false); 
        
        // create gui and start the actual application
       stateManager.attach(new GameAppState());
    }   
    
}
