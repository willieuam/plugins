package com.autotanker;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("AutoTankerConfig")

public interface AutoTankerConfig extends Config
{
	@ConfigItem(
		keyName = "eatPizza",
		name = "Eat Pizza",
		description = "Should full anchovy pizzas be automatically eaten.",
		position = 1
	)
	default boolean eatPizza()
	{
		return true;
	}

	@ConfigItem(
			keyName = "hpThresh",
			name = "HP Threshold Pizza",
			description = "HP Threshold to begin eating pizza.",
			position = 2
	)
	default int hpThresh()
	{
		return 80;
	}

	@ConfigItem(
			keyName = "combinePizza",
			name = "Combine Pizza",
			description = "Should half anchovy pizzas be automatically combined.",
			position = 3
	)
	default boolean combinePizza()
	{
		return true;
	}

	//@ConfigItem(
	//		keyName = "swapPrayer",
	//		name = "Swap Prayer",
	//		description = "Should prayer be automatically swapped."
	//)
	//default boolean swapPrayer()
	//{
	//	return true;
	//}
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