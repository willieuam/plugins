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
			keyName = "showEnabledInfoBox",
			name = "Show Infobox",
			description = "Should the infobox showing the enabled status be shown. Turn this off to hide the plugin from screenshots or recording.",
			section = generalSection,
			position = 0
	)
	default boolean showEnabledInfoBox()
	{
		return true;
	}

	@ConfigItem(
			keyName = "autoAttackType",
			name = "Attack Type",
			description = "Type of attack to do on players. If the chosen option is unavailable, its not performed (e.g. missing runes or wrong spellbook)",
			section = generalSection,
			position = 1
	)
	default AutoCasterType autoAttackType()
	{
		return AutoCasterType.TB;
	}

	@ConfigItem(
			keyName = "delay",
			name = "Delay",
			description = "Set delay between attacks. If 0, delay is 5 ticks (time between combat spell casts)",
			section = generalSection,
			position = 2
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
			position = 3
	)
	default Keybind toggleKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "castKey",
			name = "Cast",
			description = "Keybind to cast. Can use this in place of auto casting on cooldown.",
			section = generalSection,
			position = 4
	)
	default Keybind castKey()
	{
		return Keybind.NOT_SET;
	}

	@ConfigItem(
			keyName = "checkLevelRange",
			name = "Check level range Pv",
			description = "Should targets not in your level range be ignored.",
			section = generalSection,
			position = 5,
			hidden = true
	)
	default boolean checkLevelRange()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableRecast",
			name = "Recast",
			description = "When auto casting, should a spell be re-cast on the tick if you do an action to interrupt it. For example, drinking a restore after auto casting will cancel your cast. Enabling this will then cast again after you drink the restore.",
			section = generalSection,
			position = 6,
			hidden = true
	)
	default boolean enableRecast()
	{
		return true;
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
			keyName = "enableTargetList",
			name = "Use Target List",
			description = "Enable prioritizing specific players above all others. Kind of like a snipe list.",
			section = targetSection,
			position = 1
	)
	default boolean enableTargetList()
	{
		return true;
	}

	@ConfigItem(
			keyName = "targetList",
			name = "Target List",
			description = "Prioritize casting on these player names, seperated by comma and in order of importance. If cache is enabled, cached players from this list are skipped.",
			section = targetSection,
			position = 2,
			hidden = true,
			unhide = "enableTargetList"
	)
	default String targetList()
	{
		return "";
	}

	@ConfigItem(
			keyName = "enableCache",
			name = "Cache",
			description = "Save people you previously casted a spell on, and dont target them for a time\n A good use case would be TB'ing in a fight.",
			section = targetSection,
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
			section = targetSection,
			position = 4,
			hidden = true,
			unhide = "enableCache"
	)
	default int cacheDuration()
	{
		return 0;
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