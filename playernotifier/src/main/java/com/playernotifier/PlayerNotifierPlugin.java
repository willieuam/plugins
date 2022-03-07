package com.playernotifier;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.openosrs.client.game.SoundManager;
import com.openosrs.client.util.PvPUtil;
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

import java.nio.charset.StandardCharsets;
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
		if (!event.getGroup().equals("playernotifier")) { return; }

		updateWhiteList();
	}

	@Subscribe
	private void onPlayerSpawned(PlayerSpawned event) {
		final Player player = event.getPlayer();
		if (player == null) { return; }

		final Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) { return; }

		// Player spawned is local player
		if (player.getName().equalsIgnoreCase(localPlayer.getName())) { return; }

		//final String playerName = player.getName();

		// Set to only activate in PvP, and not currently in PvP
		if (config.onlyPvp() && !this.isPvpViable(player)) { return; }

		// If player is whitelisted, return
		if (playerIsWhiteListed(player)) { return; }

		// Do Auto actions according to settings

		if (config.autoLog()) {
			this.autoLog();
		}

		if (config.autoAttack() && this.isPvpViable(player)) {
			this.autoAttack(player, config.autoAttackType());
		}

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

	private void autoLog() {
		this.clientThread.invoke(() -> {
			client.invokeMenuAction(
					"Logout",
					"",
					1,
					MenuAction.CC_OP.getId(),
					-1,
					11927560
			);
		});
	}

	private void autoAttack(Player player, AutoAttack type) {
		if (type == AutoAttack.ATTACK) {
			this.autoAttackAttack(player);
		} else {
			this.autoAttackSpell(player, type);
		}
	}

	private String combatLevelCol(Actor target) {
		Player player = client.getLocalPlayer();
		if (player == null) {
			throw new IllegalArgumentException("This should not be possible.");
		}

		int combatLevelDiff = target.getCombatLevel() - player.getCombatLevel();

		if (combatLevelDiff < -10) {
			combatLevelDiff = -10;
		} else if (combatLevelDiff > 10) {
			combatLevelDiff = 10;
		}

		// https://oldschool.runescape.wiki/w/Combat_level#Colours

		switch (combatLevelDiff) {
			case (10):
				return "ff0000";
			case (9):
			case (8):
			case (7):
				return "ff3000";
			case (6):
			case (5):
			case (4):
				return "ff7000";
			case (3):
			case (2):
			case (1):
				return "ffb000";
			case (0):
				return "ffff00";
			case (-1):
			case (-2):
			case (-3):
				return "c0ff00";
			case (-4):
			case (-5):
			case (-6):
				return "80ff00";
			case (-7):
			case (-8):
			case (-9):
				return "40ff00";
			case (-10):
				return "00ff00";
			default:
				throw new IllegalArgumentException("This should not be possible.");
		}
	}

	private void autoAttackAttack(Player player) {
		this.clientThread.invoke(() -> {
			client.invokeMenuAction(
					"Attack",
					"<col=ffffff>" + player.getName() + "<col=" + this.combatLevelCol(player) + "> (level-" + player.getCombatLevel() + ")",
					player.getPlayerId(),
					MenuAction.PLAYER_SECOND_OPTION.getId(),
					0,
					0
			);
		});
	}

	private void autoAttackSpell(Player player, AutoAttack type) {
		//final int VARBIT_SPELLBOOK_HIDDEN = 6718;
		final int VARBIT_SPELLBOOK = 4070;

		final Widget widget = client.getWidget(type.getWidgetInfo());
		if (widget == null) {
			return;
		}

		if (client.getVarbitValue(VARBIT_SPELLBOOK) != type.getSpellbook() ||
				widget.getSpriteId() == type.getDisabledSpriteId() ||
				widget.getSpriteId() != type.getEnabledSpriteId()) {
			return;
		}

		this.clientThread.invoke(() -> {
			client.setSelectedSpellName("<col=00ff00>" + widget.getName() + "</col>");
			client.setSelectedSpellWidget(widget.getId());
			client.setSelectedSpellChildIndex(-1);

			client.invokeMenuAction(
				"Cast",
				"<col=00ff00>" + type.getName() + "<col=ffffff> -> <col=ffffff>" + player.getName() + "<col=" + this.combatLevelCol(player) + "> (level-" + player.getCombatLevel() + ")",
				player.getPlayerId(),
				MenuAction.SPELL_CAST_ON_PLAYER.getId(),
				0,
				0
			);
		});
	}

	private boolean isPvpViable(Player player) {
		return PvPUtil.isAttackable(client, player);
		//return (client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isAllPvpWorld(client.getWorldType()));
	}

	private boolean playerIsWhiteListed(Player player) {
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
			if (correctName(fcm.getName()).equalsIgnoreCase(player.getName())) {
				return true;
			}
		}
		return false;
	}

	private boolean playerInClan(Player player) {
		final ClanChannel clanChannel = client.getClanChannel();
		if (clanChannel == null) { return false; }

		for (ClanChannelMember ccm : clanChannel.getMembers()) {
			if (correctName(ccm.getName()).equalsIgnoreCase(player.getName())) {
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
}