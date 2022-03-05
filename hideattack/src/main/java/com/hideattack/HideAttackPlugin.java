package com.hideattack;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.clan.ClanChannel;
import net.runelite.api.clan.ClanChannelMember;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Extension
@PluginDescriptor(
	name = "Hide Attack",
	enabledByDefault = false,
	description = "Hide attack and cast options for different types of players.",
	tags = {"clan", "pvp", "cc", "friends", "chat", "fc"}
)
public class HideAttackPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private HideAttackConfig config;

	@Inject
	private KeyManager keyManager;

	@Provides
	HideAttackConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(HideAttackConfig.class);
	}

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
	public void onMenuEntryAdded(MenuEntryAdded event) {
		String target;

		if (event.getType() == MenuAction.PLAYER_SECOND_OPTION.getId()) { // attack option
			target = StringUtils.substringBetween(event.getTarget(), "<col=ffffff>", "<col=");
		} else if (event.getType() == MenuAction.SPELL_CAST_ON_PLAYER.getId()) { // cast option
			target = StringUtils.substringBetween(event.getTarget(), "<col=ffffff> -> <col=ffffff>", "<col=");
		} else {
			return;
		}

		if (!playerIsWhiteListed(target)) { return; } // player is not in the list of players to hide attack on

		client.setMenuEntries(
				removeMenuEntry(client.getMenuEntries(), event.getTarget(), event.getType())
		);

	}

	private MenuEntry[] removeMenuEntry(MenuEntry[] menuEntries, String target, int type) {
		if (menuEntries.length == 0) { return menuEntries; }

		MenuEntry[] newMenuEntries = new MenuEntry[menuEntries.length - 1];
		int j = 0;
		for (MenuEntry entry : menuEntries) {
			if (!(entry.getTarget().equals(target) && entry.getType().getId() == type)) {
				newMenuEntries[j] = entry;
				j++;
			}
		}
		return newMenuEntries;
	}

	private boolean playerIsWhiteListed(String player) {
		player = correctName(player);

		// Player is explicitly whitelisted
		if (this.playerInWhiteList(player)) { return true; }

		// Player is on friends list, and set to white list friends
		if (config.whiteListFriends() && this.playerInFriendsList(player)) { return true; }

		// Player is in friends chat, and set to white list friends chat
		if (config.whiteListChat() && this.playerInFriendsChat(player)) { return true; }

		// Player is in Clan, and set to white list clan
		if (config.whiteListClan() && this.playerInClan(player)) { return true; }

		return false;
	}

	private boolean playerInWhiteList(String player) {
		for (String p : this.whitelist) {
			if (correctName(p).equalsIgnoreCase(player)) { return true; }
		}
		return false;
	}

	private boolean playerInFriendsList(String player) {
		return client.isFriended(player, false);
	}

	public boolean playerInFriendsChat(String player) {
		final FriendsChatManager friendsChatManager = client.getFriendsChatManager();
		if (friendsChatManager == null) { return false; }

		for (FriendsChatMember fcm : friendsChatManager.getMembers()) {
			if (correctName(fcm.getName()).equalsIgnoreCase(player)) {
				return true;
			}
		}
		return false;
	}

	private boolean playerInClan(String player) {
		final ClanChannel clanChannel = client.getClanChannel();
		if (clanChannel == null) { return false; }

		for (ClanChannelMember ccm : clanChannel.getMembers()) {
			if (correctName(ccm.getName()).equalsIgnoreCase(player)) {
				return true;
			}
		}
		return false;
	}

	private String correctName(String name) { // the fc player has the wrong char code for a space compared to the other name???
		byte[] bytes = name.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i<bytes.length; i++) {
			if (bytes[i] == 63) {
				bytes[i] = 32;
			}
		}
		return new String(bytes);
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("hideattack")) { return; }

		updateWhiteList();
	}

	private void updateWhiteList() {
		this.whitelist = config.whiteList().trim().split("\\s*,\\s*");
	}
}