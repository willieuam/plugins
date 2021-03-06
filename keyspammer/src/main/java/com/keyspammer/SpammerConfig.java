package com.keyspammer;


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
			position = 1,
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
			position = 2,
			section = keybindSpammer
	)
	default Keybind msgOneKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "messageConfig2",
			position = 3,
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
			position = 4,
			section = keybindSpammer
	)
	default Keybind msgTwoKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "messageConfig3",
			position = 5,
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
			position = 6,
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
			keyName = "checkTargetIsPlayer",
			name = "Only Players",
			description = "Check that the target for pile spam is a player.",
			position = 3,
			section = pileSpammerSection
	)
	default boolean checkTargetIsPlayer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "pileKeybind",
			name = "Pile Keybind",
			description = "Configure what button to press to spam your current target",
			position = 4,
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
			position = 5,
			section = pileSpammerSection
	)
	default Keybind autoPileSpamKeybind()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "autoPileSpamDelay",
			name = "Delay",
			description = "Tick delay between auto pile spam messages.",
			position = 6,
			section = pileSpammerSection
	)
	default int autoPileSpamDelay()
	{
		return 4;
	}

	@ConfigItem(
			keyName = "ignorePrefix",
			name = "Ignore Prefix",
			description = "Ignore specific prefixes, such as clan tags.",
			position = 7,
			section = pileSpammerSection
	)
	default boolean ignorePrefix()
	{
		return false;
	}

	@ConfigItem(
			keyName = "ignoredPrefixes",
			name = "Prefixes",
			description = "Prefixes to ignore, separated by comma. Not case sensitive.",
			position = 8,
			section = pileSpammerSection,
			hidden = true,
			unhide = "ignorePrefix"
	)
	default String ignoredPrefixes()
	{
		return "";
	}
}
