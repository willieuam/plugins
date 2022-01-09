package com.missingplayers;

import net.runelite.client.config.*;

@ConfigGroup("missingplayers")
public interface MissingPlayersConfig extends Config
{
    @ConfigItem(
            keyName = "missingPlayersKeyBind",
            name = "Key",
            description = "Configure what button to press to get missing players",
            position = 0
    )
    default Keybind missingPlayersKeyBind()
    {
        return Keybind.NOT_SET;
    }
}
