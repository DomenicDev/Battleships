package de.domenic.battleships;

import de.domenic.battleships.game.Position;
import de.domenic.battleships.game.GameSettings;
import de.domenic.battleships.game.Ship;
import de.domenic.battleships.game.GameEntity;
import com.jme3.network.serializing.Serializer;
import de.domenic.battleships.messages.AttackMessage;
import de.domenic.battleships.messages.AttackResultMessage;
import de.domenic.battleships.messages.CloseGameMessage;
import de.domenic.battleships.messages.FinishedLoadingGameMessage;
import de.domenic.battleships.messages.GameDecisionMessage;
import de.domenic.battleships.messages.GameStartMessage;
import de.domenic.battleships.messages.JoinGameMessage;
import de.domenic.battleships.messages.LeaveGameMessage;
import de.domenic.battleships.messages.LoadGameMessage;
import de.domenic.battleships.messages.PlaceShipMessage;
import de.domenic.battleships.messages.PlayerTurnsMessage;
import de.domenic.battleships.messages.RequestPlacingMessage;
import de.domenic.battleships.messages.ShipArrayMessage;
import de.domenic.battleships.messages.ShipDestroyedMessage;
import de.domenic.battleships.messages.StateChangedMessage;
import de.domenic.battleships.messages.FinishedPlacingMessage;

/**
 *
 * @author Domenic
 */
public class NetworkUtils {
    
    
    public static final String REASON_FOR_WAITING_1 = "The other player still needs time to place its ships.";
    public static final String REASON_FOR_WAITING_2 = "Waiting for another player.";
    
    
    public static void initSerializers() {
        Serializer.registerClass(JoinGameMessage.class);
        Serializer.registerClass(Position.class);
        Serializer.registerClass(Ship.class);
        Serializer.registerClass(RequestPlacingMessage.class);
        Serializer.registerClass(AttackMessage.class);
        Serializer.registerClass(GameEntity.class);
        Serializer.registerClass(ShipArrayMessage.class);
        Serializer.registerClass(PlaceShipMessage.class);
        Serializer.registerClass(FinishedPlacingMessage.class);
        Serializer.registerClass(PlayerTurnsMessage.class);
        Serializer.registerClass(AttackResultMessage.class);    
        Serializer.registerClass(ShipDestroyedMessage.class);
        Serializer.registerClass(StateChangedMessage.class);
        Serializer.registerClass(GameDecisionMessage.class);
        Serializer.registerClass(LoadGameMessage.class);
        Serializer.registerClass(GameSettings.class);
        Serializer.registerClass(FinishedLoadingGameMessage.class);
        Serializer.registerClass(GameStartMessage.class);
        Serializer.registerClass(CloseGameMessage.class);
        Serializer.registerClass(LeaveGameMessage.class);
    }
    
}
