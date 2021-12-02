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

package net.runelite.client.plugins.cwa;


import net.runelite.api.SoundEffectVolume;
import net.runelite.client.config.*;

@ConfigGroup("clanwars")
public interface CwaConfig extends Config
{
	int VOLUME_MAX = SoundEffectVolume.HIGH;

	@ConfigSection(
			name = "Timers/Notifcations",
			description = "",
			position = 0,
			keyName = "notifs"
	)
	String notificationSection = "notificationSection";

	@ConfigItem(
			keyName = "showCwaTimer",
			name = "Display Challenge Timer",
			description = "Displays the remaining time left for the round to start",
			position = 1,
			section = notificationSection
	)
	default boolean showCwaTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showCwaNofifs",
			name = "Challenge Notifications",
			description = "Displays a notification when your clan receives or accepts a CWA Challenge",
			position = 2,
			section = notificationSection
	)
	default boolean showCwaNotifs(){return true;}

	@ConfigItem(
			keyName = "showScimTimer",
			name = "Dragon Scim Timer",
			description = "Displays a countdown of when you're able to use overheads",
			position = 3,
			section = notificationSection
	)
	default boolean showScimTimer()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showScimOverlay",
			name = "Dragon Scim Border Overlay",
			description = "Displays an overlay that will disappear when you're able to use overheads",
			position = 4,
			section = notificationSection
	)
	default boolean showScimOverLay()
	{
		return true;
	}


	@ConfigItem(
			position = 5,
			keyName = "dragonScimSpecSound",
			name = "Dragon Scim Spec Noise",
			description = "play a sound when a dragon scim spec has been used on you",
			section = notificationSection
	)
	default boolean dScimSound()
	{
		return false;
	}

	@ConfigItem(
			position = 6,
			keyName = "pneckBreak",
			name = "Phoenix Necklace Break Noise",
			description = "play a sound when your pneck breaks",
			section = notificationSection
	)
	default boolean pneckBreak()
	{
		return false;
	}

	@Range(
			max = VOLUME_MAX
	)
	@ConfigItem(
			keyName = "scimVolume",
			name = "Scim Spec Volume",
			description = "Configures the volume of the scim sound. A value of 0 will disable tick sounds.",
			position = 7,
			section = notificationSection
	)
	default int dScimSpecVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}




	@Range(
			max = VOLUME_MAX
	)
	@ConfigItem(
			keyName = "neckVolume",
			name = "Necklace Volume",
			description = "Configures the volume of the break sound. A value of 0 will disable tick sounds.",
			position = 8,
			section = notificationSection
	)
	default int neckBreakVolume()
	{
		return SoundEffectVolume.MEDIUM_HIGH;
	}
}
