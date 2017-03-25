package de.domenic.battleships.gui;

import de.domenic.battleships.appstates.GameAppState;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.screen.Screen;

/**
 *
 * @author Domenic
 */
public class ConnectionScreenController extends AbstractScreenController {
    
    private TextField address;
    private TextField port;    
    
    public ConnectionScreenController(GameAppState gameState) {
        super(gameState);
    }

    @Override
    public void bind(Nifty nifty, Screen screen) {
        super.bind(nifty, screen);
        address = screen.findNiftyControl("addressTextField", TextField.class);
        port = screen.findNiftyControl("portTextField", TextField.class);
    }
    
    public void connect() {
        String a = this.address.getRealText();
        String p = this.port.getRealText();
        
        if (a.isEmpty() || p.isEmpty()) {
            return;
        }
        
        int portInt = Integer.parseInt(p);
        
        gameState.connect(a, portInt);
        
    }
    
    public void cancel() {
        gameState.backToMainMenu();
    }
    
}
