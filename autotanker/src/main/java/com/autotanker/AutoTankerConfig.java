package com.autotanker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("AutoTankerConfig")

public interface AutoTankerConfig extends Config
{
	@ConfigItem(
		keyName = "eatPizza",
		name = "Eat Pizza",
		description = "Should full anchovy pizzas be automatically eaten.",
		position = 0
	)
	default boolean eatPizza()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hpThresh",
			name = "HP Threshold Pizza",
			description = "HP Threshold to begin eating pizza.",
			position = 1
	)
	default int hpThresh()
	{
		return 80;
	}

	@ConfigItem(
			keyName = "combinePizza",
			name = "Combine Pizza",
			description = "Should half anchovy pizzas be automatically combined.",
			position = 2
	)
	default boolean combinePizza()
	{
		return true;
	}

	@ConfigItem(
			keyName = "swapPrayer",
			name = "Swap Prayer",
			description = "Should prayer be automatically swapped.",
			position = 3
	)
	default boolean swapPrayer()
	{
		return false;
	}

	@ConfigItem(
			keyName = "teamMode",
			name = "Team Mode",
			description = "What players count as being on your team for the prayer swaps.",
			position = 4
	)
	default TeamMode teamMode()
	{
		return TeamMode.CAPE;
	}

	@ConfigSection(
			name = "Failsafe",
			description = "",
			position = 5,
			keyName = "failsafeSection"
	)
	String failsafeSection = "failsafeSection";

	@ConfigItem(
			keyName = "onlyF2P",
			name = "Only F2P (NOT WORKING)",
			description = "Disable the plugin outside of F2P worlds.",
			position = 0,
			section = failsafeSection,
			hidden = true
	)
	default boolean onlyF2P()
	{
		return true;
	}

	@ConfigItem(
			keyName = "onlyCWA",
			name = "Only CWA",
			description = "Disable the plugin outside of Clan Wars Arena.",
			position = 1,
			section = failsafeSection
	)
	default boolean onlyCWA()
	{
		return false;
	}

	//
	//@ConfigItem(
	//		keyName = "enableDebug",
	//		name = "Debug Prayer",
	//		description = "Show debug output."
	//)
	//default boolean enableDebug()
	//{
	//	return true;
	//}
	//
	//@ConfigItem(
	//		keyName = "autoTele",
	//		name = "Auto Tele Varrock",
	//		description = "Automatically try to Varrock teleport when health reaches a threshold or out of food."
	//)
	//default boolean autoTele()
	//{
	//	return true;
	//}
	//
	//@ConfigItem(
	//		keyName = "teleThresh",
	//		name = "Teleport HP Threshhold Tele",
	//		description = "HP Threshhold to try and teleport at."
	//)
	//default int teleThresh()
	//{
	//	return 20;
	//}
}