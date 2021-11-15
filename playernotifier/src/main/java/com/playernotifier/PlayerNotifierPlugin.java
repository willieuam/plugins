package com.playernotifier;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.openosrs.client.game.SoundManager;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.*;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.Notifier;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import java.util.Collection;

@Extension
@PluginDescriptor(
	name = "[w] Player Notifier",
	description = "Player Notifier",
	tags = {"player", "notifier", "pker"},
	enabledByDefault = false
)
@Slf4j
public class PlayerNotifierPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private PlayerNotifierConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private SoundManager soundManager;

	@Inject
	private Notifier notifier;

	// Provides our config
	@Provides
	PlayerNotifierConfig provideConfig(ConfigManager configManager) { return configManager.getConfig(PlayerNotifierConfig.class); }

	protected String[] whitelist;

	@Override
	protected void startUp() {
		this.updateWhiteList();
	}

	@Override
	protected void shutDown() {
		this.whitelist = null;

	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("PlayerNotifierConfig")) { return; }

		updateWhiteList();
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event) {
		final Player player = event.getPlayer();

		if (player == null) { return; }

		//final String playerName = player.getName();

		// Set to only activate in PvP, and not currently in PvP
		if (config.onlyPvp() && !this.isPvp()) { return; }

		// Player is explicitly whitelisted
		if (this.playerInWhiteList(player)) { return; }

		// Player is on friends list, and set to white list friends
		if (config.whiteListFriends() && this.playerInFriendsList(player)) { return; }

		// Player is in friends chat, and set to white list friends chat
		if (config.whiteListChat() && this.playerInFriendsChat(player)) { return; }

		// Player is in Clan, and set to white list clan
		if (config.whiteListClan() && this.playerInClan(player)) { return; }

		// Do notifications according to settings

		if (config.clientNotif()) {
			this.clientNotif(player);
		}

		if (config.soundNotif()) {
			this.soundNotif();
		}

		if (config.chatNotif()) {
			this.chatNotif(player);
		}
	}

	@Subscribe
	protected void onSoundEffectPlayed(SoundEffectPlayed event) {
		if (event == null) { return; }
		log.info(String.valueOf(event.getSoundId()));
	}

	private void updateWhiteList() {
		this.whitelist = config.whiteList().trim().split("\\s*,\\s*");
	}

	private void chatNotif(Player player) {
		final String message = new ChatMessageBuilder()
				.append(ChatColorType.NORMAL)
				.append("Player ")
				.append(ChatColorType.HIGHLIGHT)
				.append(player.getName())
				.append(" (level-")
				.append(String.valueOf(player.getCombatLevel()))
				.append(")")
				.append(ChatColorType.NORMAL)
				.append(" spawned!")
				.build();

		chatMessageManager.queue(
				QueuedMessage.builder()
						.type(ChatMessageType.CONSOLE)
						.runeLiteFormattedMessage(message)
						.build());
	}

	private void soundNotif() {
		client.playSoundEffect(SoundEffectID.TOWN_CRIER_BELL_DONG, config.soundNotifVolume());
	}

	private void clientNotif(Player player) {
		notifier.notify("Player " +
						player.getName() +
						" (level-" +
						player.getCombatLevel() +
						")" +
						" spawned!"
				);
	}

	private boolean isPvp() {
		return (client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isAllPvpWorld(client.getWorldType()));
	}

	private boolean playerInWhiteList(Player player) {
		for (String p : this.whitelist) {
			if (p.equalsIgnoreCase(player.getName())) { return true; }
		}
		return false;
	}

	private boolean playerInFriendsList(Player player) {
		return client.isFriended(player.getName(), false);
	}

	public boolean playerInFriendsChat(Player player) {
		final FriendsChatManager friendsChatManager = client.getFriendsChatManager();
		if (friendsChatManager == null) { return false; }

		for (FriendsChatMember fcm : friendsChatManager.getMembers()) {
			if (fcm.getName().equalsIgnoreCase(player.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean playerInClan(Player player) {
		final ClanChannel clanChannel = client.getClanChannel();
		if (clanChannel == null) { return false; }

		for (ClanChannelMember ccm : clanChannel.getMembers()) {
			if (ccm.getName().equalsIgnoreCase(player.getName())) {
				return true;
			}
		}
		return false;
	}
}