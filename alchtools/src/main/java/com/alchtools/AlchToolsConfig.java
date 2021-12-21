/*
 * Copyright (c) 2018, Andrew EP | ElPinche256 <https://github.com/ElPinche256>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.alchtools;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("AlchToolsConfig")

public interface AlchToolsConfig extends Config
{
	@ConfigItem(
		keyName = "alchMenuOption",
		name = "Alch Menu Option",
		description = "Enable menu option on selected items to 1-click alch"
	)
	default boolean alchMenuOption()
	{
		return false;
	}

	@ConfigItem(
			keyName = "items",
			name = "Target Items",
			description = "Items to add 1-click alch menu option on."
	)
	default String items()
	{
		return "";
	}

	@ConfigItem(
			keyName = "autoAlch",
			name = "Auto Alch",
			description = "Automatically alch."
	)
	default boolean autoAlch()
	{
		return false;
	}

	@ConfigItem(
			keyName = "autoAlchItemId",
			name = "Target Item",
			description = "Item to auto alch."
	)
	default int autoAlchItemId()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "autoAlchBind",
			name = "Start/Stop auto alching keybind",
			description = ""
	)
	default Keybind autoAlchBind()
	{
		return Keybind.NOT_SET;
	}
}