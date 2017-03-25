package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Domenic
 */
public class HudScreenController extends AbstractScreenController {
    
    private Element exitPopup;    
    private Label stateLabel;
    
    public HudScreenController(GameAppState gameState) {
        super(gameState);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        super.bind(nifty, screen); 
        exitPopup = nifty.createPopup("popupExit");
        stateLabel = screen.findNiftyControl("stateLabel", Label.class);
    }    
    
    public void click(boolean exit) {
        if (exit) {
            gameState.backToMainMenu();
        } else {
            nifty.closePopup(exitPopup.getId());
        }
    }
    
    public void setState(String state) {
        stateLabel.getElement().getRenderer(TextRenderer.class).setText("State: " + state);
    }
    
    public void showExitPopup() {
        nifty.showPopup(nifty.getCurrentScreen(), exitPopup.getId(), null);
    }
    
}
