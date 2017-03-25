package de.domenic.battleships.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.domenic.battleships.gui.AbstractScreenController;
import de.domenic.battleships.gui.ConnectionScreenController;
import de.domenic.battleships.gui.HostGameScreenController;
import de.domenic.battleships.gui.HudScreenController;
import de.domenic.battleships.gui.LobbyScreenController;
import de.domenic.battleships.gui.MainMenuScreenController;
import de.domenic.battleships.gui.SingleplayerScreenController;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author Domenic
 */
public class NiftyAppState extends AbstractAppState {

    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Application app;
    private GameAppState gameAppState;
    
    
    
    public NiftyAppState(GameAppState gameAppState) {
        this.gameAppState = gameAppState;
    }

   

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        niftyDisplay = new NiftyJmeDisplay(app.getAssetManager(), app.getInputManager(), app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/Screens/newscreen.xml", "start", 
                new MainMenuScreenController(gameAppState),
                new SingleplayerScreenController(gameAppState),
                new ConnectionScreenController(gameAppState),
                new HostGameScreenController(gameAppState),
                new LobbyScreenController(gameAppState),
                new HudScreenController(gameAppState));
        
        app.getGuiViewPort().addProcessor(niftyDisplay);
    }
    
    public void goToScreen(String screen) {
        nifty.gotoScreen(screen);
    }
  
    public <T extends AbstractScreenController> T getScreenController(Class<T> c) {
        for (de.domenic.battleships.gui.Screen s : de.domenic.battleships.gui.Screen.values()) {
            String id = s.getIdName();
            Screen screen = nifty.getScreen(id);
            if (screen != null) {
                ScreenController controller = screen.getScreenController();
                if (c.isAssignableFrom(controller.getClass())) {
                    return (T) controller;
                }
            }
        }
        return null;
    }

    public boolean isCurrentScreen(de.domenic.battleships.gui.Screen screen) {
        return screen.getIdName().equals(nifty.getCurrentScreen().getScreenId());
    }

}
