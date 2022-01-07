package com.wautotyper;

import net.runelite.client.config.*;

@ConfigGroup("wautotyper")
public interface wAutoTyperConfig extends Config
{
	@ConfigItem(
			keyName = "messages",
			name = "Messages",
			description = "List of messages to type, one per line."
	)
	default String messages()
	{
		return "";
	}

	@ConfigItem(
			keyName = "toggleKeyBind",
			name = "Toggle",
			description = "Button to toggle auto typing."
	)
	default Keybind toggleKeyBind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "delay",
			name = "Tick Delay",
			description = "Tick delay between messages."
	)
	default int delay()
	{
		return 4;
	}


}
