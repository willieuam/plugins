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