package de.domenic.battleships.appstates;

import de.domenic.battleships.game.Direction;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.messages.AttackMessage;
import de.domenic.battleships.messages.FinishedLoadingGameMessage;
import de.domenic.battleships.messages.RequestPlacingMessage;

/**
 *
 * @author Domenic
 */
public class MultiplayerGame extends AbstractGame {
    
    private ClientAppState clientAppState;

    public MultiplayerGame(GameSettings settings) {
        super(settings);
    }    

    public void setClientAppState(ClientAppState clientAppState) {
        this.clientAppState = clientAppState;
    }

    public ClientAppState getClientAppState() {
        return clientAppState;
    }
   

    @Override
    protected void requestPlacing(Ship shipToPlace, Position placePos, Direction dir) {
        RequestPlacingMessage pm = new RequestPlacingMessage(shipToPlace.getId(), placePos, dir);
        clientAppState.sendMessage(pm);
    }

    @Override
    protected void requestShoot(Position attackedPosition) {
        if (attackedPosition != null) {
            AttackMessage am = new AttackMessage(clientAppState.getID(), attackedPosition);
            clientAppState.sendMessage(am);
        }
    }

    @Override
    public void onFinishedLoadingGame() {
        clientAppState.sendMessage(new FinishedLoadingGameMessage(clientAppState.getID()));
        
        // not clean but ...
        visualAppState.getVisualGameFieldByPlayer(playerTwo).setVisible(false);
        visualAppState.getVisualGameFieldByPlayer(playerOne).setVisible(true);
    }

}
