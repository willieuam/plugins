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
package com.playernotifier;

import com.openosrs.client.game.AttackStyle;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("PlayerNotifierConfig")

public interface PlayerNotifierConfig extends Config
{
	@ConfigItem(
			keyName = "onlyPvp",
			name = "Only Notify in PvP",
			description = "Only notify in PvP enabled zones (PvP worlds, Wilderness).",
			position = 0
	)
	default boolean onlyPvp()
	{
		return true;
	}

	@ConfigItem(
			keyName = "clientNotif",
			name = "Client Notification",
			description = "Use the Client notifications system.",
			position = 1
	)
	default boolean clientNotif()
	{
		return true;
	}

	@ConfigItem(
			keyName = "soundNotif",
			name = "Sound Notification",
			description = "Play a sound when notifying.",
			position = 2
	)
	default boolean soundNotif()
	{
		return true;
	}

	@ConfigItem(
			keyName = "soundNotifVolume",
			name = "Sound Volume",
			description = "Volume level when playing sound notif.",
			position = 3
	)
	default int soundNotifVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}

	@ConfigItem(
			keyName = "chatNotif",
			name = "Chat Notification",
			description = "Put a message into the chat when notifying.",
			position = 4
	)
	default boolean chatNotif()
	{
		return true;
	}

	@ConfigSection(
			name = "Auto",
			description = "",
			position = 5,
			keyName = "autoSection"
	)
	String autoSection = "autoSection";

	@ConfigItem(
			keyName = "autoLog",
			name = "Auto Log",
			description = "Automatically log out if notifying.",
			section = "autoSection",
			position = 0
	)
	default boolean autoLog()
	{
		return false;
	}

	@ConfigItem(
			keyName = "autoAttack",
			name = "Auto Attack",
			description = "Automatically attack player if notifying.",
			section = "autoSection",
			position = 1
	)
	default boolean autoAttack()
	{
		return false;
	}

	@ConfigItem(
			keyName = "autoAttackType",
			name = "Attack Type",
			description = "Type of attack to do on player if Auto Attack is enabled. If the chosen option is unavailable, its not performed (e.g. missing runes or wrong spellbok)",
			section = "autoSection",
			position = 2
	)
	default AutoAttack autoAttackType()
	{
		return AutoAttack.ATTACK;
	}

	@ConfigSection(
			name = "Whitelist",
			description = "",
			position = 6,
			keyName = "whitelistSection"
	)
	String whitelistSection = "whitelistSection";

	@ConfigItem(
		keyName = "whiteList",
		name = "White List",
		description = "Names to always White List. Separate with a comma (,).",
		section = "whitelistSection",
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
			section = "whitelistSection",
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
			section = "whitelistSection",
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
			section = "whitelistSection",
			position = 3
	)
	default boolean whiteListClan()
	{
		return true;
	}
}