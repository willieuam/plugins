package com.wpneck;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("wpneck")
public interface wPneckConfig extends Config
{
	@ConfigItem(
		keyName = "mode",
		name = "mode",
		description = "",
		position = 1
	)
	default wPneckMode mode()
	{
		return wPneckMode.ONBREAK;
	}

}