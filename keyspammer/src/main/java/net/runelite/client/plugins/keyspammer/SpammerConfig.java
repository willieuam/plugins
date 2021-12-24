/*
 * Copyright (c) 2019, St0newall
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

package net.runelite.client.plugins.keyspammer;


import net.runelite.client.config.*;

@ConfigGroup("keyspammer")
public interface SpammerConfig extends Config
{
	@ConfigSection(
			name = "Pile Spammer",
			description = "",
			position = 0,
			keyName = "pileSpammerSection"
	)
	String pileSpammerSection = "pileSpammerSection";

	@ConfigSection(
			name = "Keybind Spammer",
			description = "",
			position = 1,
			keyName = "keybindSpammer"
	)
	String keybindSpammer = "keybindSpammer";

	@ConfigItem(
			keyName = "messageConfig1",
			position = 2,
			name = "Message One",
			description = "A message you would like to spam upon keypress",
			section = keybindSpammer
	)
	default String messageOne()
	{
		return "";
	}

	@ConfigItem(
			keyName = "keybind1",
			name = "Message One Keybind",
			description = "Configure what button to press to spam",
			position = 3,
			section = keybindSpammer
	)
	default Keybind msgOneKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "messageConfig2",
			position = 4,
			name = "Message Two",
			description = "A message you would like to spam upon keypress",
			section = keybindSpammer
	)
	default String messageTwo()
	{
		return "";
	}

	@ConfigItem(
			keyName = "keybind2",
			name = "Message Two Keybind",
			description = "Configure what button to press to spam",
			position = 5,
			section = keybindSpammer
	)
	default Keybind msgTwoKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "messageConfig3",
			position = 6,
			name = "Message Three",
			description = "A message you would like to spam upon keypress",
			section = keybindSpammer
	)
	default String messageThree()
	{
		return "";
	}

	@ConfigItem(
			keyName = "keybind3",
			name = "Message Three Keybind",
			description = "Configure what button to press to spam",
			position = 7,
			section = keybindSpammer
	)
	default Keybind msgThreeKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "clanPrefix",
			position = 1,
			name = "Clan Prefix",
			description = "The clan prefix you would like to spam before your targets name",
			section = pileSpammerSection
	)
	default String clanPrefix()
	{
		return "";
	}

	@ConfigItem(
			keyName = "useFirstFourChars",
			name = "Smaller Pile Names",
			description = "Spam the first four characters of a piles name opposed to the full name",
			position = 2,
			section = pileSpammerSection
	)
	default boolean smallerPileNames()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pileKeybind",
			name = "Pile Keybind",
			description = "Configure what button to press to spam your current target",
			position = 3,
			section = pileSpammerSection
	)
	default Keybind pilekeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "autoPileSpamToggle",
			name = "Auto Pile Spam",
			description = "Configure what button to press to automatically spam your current target",
			position = 4,
			section = pileSpammerSection
	)
	default Keybind autoPileSpamKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "autoPileSpamDelay",
			name = "Auto Pile Spam Tick Delay",
			description = "Tick delay between auto pile spam messages.",
			position = 5,
			section = pileSpammerSection
	)
	default int autoPileSpamDelay()
	{
		return 4;
	}
}
