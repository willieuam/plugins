package com.hideattack;

import net.runelite.client.config.*;

@ConfigGroup("hideattack")
public interface HideAttackConfig extends Config
{
    @ConfigItem(
            keyName = "whiteList",
            name = "White List",
            description = "Names to always White List. Separate with a comma (,).",
            position = 0
    )
    default String whiteList()
    {
        return "";
    }

    @ConfigItem(
            keyName = "whiteListFriends",
            name = "White List Friends",
            description = "Always White List players on Friends List.",
            position = 1
    )
    default boolean whiteListFriends()
    {
        return true;
    }

    @ConfigItem(
            keyName = "whiteListChat",
            name = "White List Friends Chat",
            description = "Always White List players in Friends Chat.",
            position = 2
    )
    default boolean whiteListChat()
    {
        return true;
    }

    @ConfigItem(
            keyName = "whiteListClan",
            name = "White List Clan",
            description = "Always White List players in Clan.",
            position = 3
    )
    default boolean whiteListClan()
    {
        return true;
    }
}
