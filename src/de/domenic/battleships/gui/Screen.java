package de.domenic.battleships.gui;

/**
 *
 * @author Domenic
 */
public enum Screen {

    Empty("empty"),
    HUD("hud"),
    MainMenu("start"),
    SingleplayerScreen("singleplayerScreen"),
    ConnectionScreen("connectionScreen"),
    HostGameScreen("hostMultiplayerScreen"),
    LobbyScreen("lobbyScreen"),
    Credits("credits");

    private Screen(String id) {
        this.id = id;
    }

    private final String id;

    public String getIdName() {
        return this.id;
    }
}
