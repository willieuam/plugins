package com.autocaster;

import com.openosrs.client.game.AttackStyle;
import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

@ConfigGroup("autocaster")
public interface AutoCasterConfig extends Config
{
	@ConfigSection(
			name = "General",
			description = "",
			position = 0,
			keyName = "generalSection"
	)
	String generalSection = "generalSection";

	@ConfigSection(
			name = "Targets",
			description = "",
			position = 1,
			keyName = "whitelistSection"
	)
	String targetSection = "targetSection";

	@ConfigSection(
			name = "Whitelist",
			description = "",
			position = 2,
			keyName = "whitelistSection"
	)
	String whitelistSection = "whitelistSection";

	@ConfigItem(
			keyName = "autoAttackType",
			name = "Attack Type",
			description = "Type of attack to do on players. If the chosen option is unavailable, its not performed (e.g. missing runes or wrong spellbook)",
			section = generalSection,
			position = 0
	)
	default AutoAttack autoAttackType()
	{
		return AutoAttack.TB;
	}

	@ConfigItem(
			keyName = "delay",
			name = "Delay",
			description = "Set delay between attacks. If 0, delay is 5 ticks (time between combat spell casts)",
			section = generalSection,
			position = 1
	)
	default int delay()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "toggleKey",
			name = "Toggle",
			description = "Keybind to enable/disable auto spell casting",
			section = generalSection,
			position = 2
	)
	default Keybind toggleKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "enableCache",
			name = "Cache",
			description = "Save people you previously casted a spell on, and dont target them for a time\n A good use case would be TB'ing in a fight.",
			section = generalSection,
			position = 3
	)
	default boolean enableCache()
	{
		return false;
	}

	@ConfigItem(
			keyName = "cacheDuration",
			name = "Cache Time",
			description = "Time (in game ticks) to save players in cache. If 0, the default is 250 ticks.",
			section = generalSection,
			position = 4
	)
	default int cacheDuration()
	{
		return 0;
	}

	@ConfigItem(
			keyName = "targetType",
			name = "Target Type",
			description = "targetType",
			section = targetSection,
			position = 0
	)
	default TargetType targetType()
	{
		return TargetType.RANDOM;
	}

	@ConfigItem(
		keyName = "whiteList",
		name = "White List",
		description = "Names to always White List. Separate with a comma (,).",
		section = whitelistSection,
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
			section = whitelistSection,
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
			section = whitelistSection,
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
			section = whitelistSection,
			position = 3
	)
	default boolean whiteListClan()
	{
		return true;
	}
}