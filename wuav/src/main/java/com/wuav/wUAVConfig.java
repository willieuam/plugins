package com.wuav;

import net.runelite.client.config.*;

@ConfigGroup("wuav")
public interface wUAVConfig extends Config
{
    @ConfigItem(
            keyName = "ccPlayersKeybind",
            name = "Log CC",
            description = "Configure what button to press to log CC. Logs players the moment the key is pressed.",
            position = 0
    )
    default Keybind ccPlayersKeybind()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "inGamePlayersKeybind",
            name = "Log In Game",
            description = "Configure what button to press to log in game players. Press once to begin log, press again to save current list.",
            position = 1
    )
    default Keybind inGamePlayersKeybind()
    {
        return Keybind.NOT_SET;
    }
}
