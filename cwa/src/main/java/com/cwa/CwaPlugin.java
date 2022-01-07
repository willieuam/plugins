package com.cwa;

import javax.inject.Inject;

import com.google.inject.Provides;
import com.openosrs.client.game.Sound;
import com.openosrs.client.game.SoundManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.kit.KitType;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.util.Objects;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Clan Wars Arena",
	enabledByDefault = false,
	description = "plugins for CWA",
	tags = {"Clan Wars", "Wilderness", "location", "CWA", "pvp"}
)

// shoutouts to OP from ly

public class CwaPlugin extends Plugin
{
	private static final String DRAGON_SCIM_MESSAGE = "You've been injured and can't use protection prayers!";
	private static final String PHOENIX_NECKLACE_BREAK = "Your phoenix necklace heals you, but is destroyed in the process.";
	private static final String CWA_CHALLENGE_REQUEST = "wishes to challenge your chat-channel to a Clan War";
	private static final String CWA_CHALLENGER_MESSAGE = "Your chat-channel is being invited to join you...";
	private static final String CWA_CHALLENGE_MESSAGE = "Your chat-channel has initiated a battle! Come to the Clan Wars challenge area to join it.";


	private CwaTimer cwaTimer;
	private DragonScimTimer scimTimer;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Client client;

	@Inject
	private SoundManager soundManager;

	@Inject
	private CwaConfig config;

	@Inject
	private Notifier notifier;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private DragonScimOverlay overlay;

	@Provides
	CwaConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CwaConfig.class);
	}


	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if ( chatMessage.getMessage().contains(DRAGON_SCIM_MESSAGE) && config.showScimTimer() )
		{
			createScimTimer();
			if (config.showScimOverLay())
			{
				overlayManager.add(overlay);
			}
			if (config.dScimSound())
			{
				client.playSoundEffect(SoundEffectID.MINING_TINK, config.dScimSpecVolume());
			}
		}
		if ( chatMessage.getMessage().contains(PHOENIX_NECKLACE_BREAK) && config.pneckBreak() )
		{
			client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DONG, config.neckBreakVolume());
		}
		if ( chatMessage.getMessage().contains(CWA_CHALLENGE_REQUEST) && config.showCwaNotifs() )
		{
			notifier.notify("A player is requesting a challenge against your clan");
		}
		if ( chatMessage.getMessage().contains(CWA_CHALLENGE_MESSAGE) && config.showCwaTimer() && config.showCwaNotifs() )
		{
			notifier.notify("You have two minutes to get into the CWA portal");
			createCWATimer();
		}
		if (chatMessage.getMessage().contains(CWA_CHALLENGER_MESSAGE) && config.showCwaTimer() )
		{
			createCWATimer();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		if ( scimTimer != null && scimTimer.getTimeLeft() == 0)
		{
			overlayManager.remove(overlay);
		}
	}
	private void createCWATimer()
	{
		cwaTimer = new CwaTimer(this, itemManager);
		infoBoxManager.addInfoBox(cwaTimer);
	}

	private void createScimTimer()
	{
		scimTimer = new DragonScimTimer(this, itemManager);
		infoBoxManager.addInfoBox(scimTimer);
	}
}