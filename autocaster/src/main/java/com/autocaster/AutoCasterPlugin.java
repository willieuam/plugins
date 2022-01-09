package com.autocaster;

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
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

import java.util.*;

@Extension
@PluginDescriptor(
	name = "[w] Auto Caster",
	description = "Automatically cast spells on targets around you.",
	tags = {"player", "spell", "pk", "clan", "pvp"},
	enabledByDefault = false
)
@Slf4j
public class AutoCasterPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private AutoCasterConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	// Provides our config
	@Provides
	AutoCasterConfig provideConfig(ConfigManager configManager) { return configManager.getConfig(AutoCasterConfig.class); }

	private boolean enabled;
	private String[] whitelist;
	private Map<String, Integer> cache;
	private static final int cacheDuration = 250; // 250 ticks = 2:30
	private int delay;
	private static final int COMBAT_SPELL_DELAY = 5;

	@Override
	protected void startUp() {
		this.updateWhiteList();
		this.cache = new HashMap<>();
		enabled = false;
		delay = 0;
		keyManager.registerKeyListener(toggleKeyBindListener);
	}

	@Override
	protected void shutDown() {
		this.whitelist = null;
		this.cache = null;
		enabled = false;
		delay = 0;
		keyManager.unregisterKeyListener(toggleKeyBindListener);
	}

	private final HotkeyListener toggleKeyBindListener = new HotkeyListener(() -> config.toggleKey())
	{
		@Override
		public void hotkeyPressed()
		{
			enabled = !enabled;
			log.info("Toggle: " + enabled);
		}
	};

	@Subscribe
	protected void onGameTick(GameTick event) {
		log.info("onGameTick; Delay = " + delay);
		log.info("Cache: " + cache.toString());

		tickCache();
		delay--;
		if (delay < 0) { delay = 0; }

		if (!enabled || delay != 0) { return; }

		Player target = target();
		log.info("Target: " + target.getName());
		autoAttackSpell(target, config.autoAttackType());
		if (config.enableCache()) { addPlayerToCache(target); log.info("Cached"); }
		delay = config.delay() == 0 ? COMBAT_SPELL_DELAY : config.delay();
	}

	private List<Player> targets() {
		log.info("---Get Targets---");
		List<Player> targets = new ArrayList<>();
		for (Player p : client.getPlayers()) {
			if (p != client.getLocalPlayer() &&
				!playerIsWhiteListed(p) &&
				!playerInCache(p)) {
				targets.add(p);
				log.info(p.getName());
			}
		}
		log.info("---End Targets---");
		return targets;
	}

	private Player target() {
		List<Player> targets = targets();
		Random r = new Random();
		return targets.get(r.nextInt(targets.size()));
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

	private boolean isPvp() {
		return (client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isAllPvpWorld(client.getWorldType()));
	}

	private void addPlayerToCache(Player player) {
		cache.put(Objects.requireNonNull(player.getName()).toLowerCase(), config.cacheDuration() == 0 ? cacheDuration : config.cacheDuration());
	}

	private boolean playerInCache(Player player) {
		return cache.containsKey(Objects.requireNonNull(player.getName()).toLowerCase());
	}

	private void tickCache() {
		for (String p : cache.keySet()) {
			cache.put(p, (cache.get(p) - 1));
			if (cache.get(p) == 0) { cache.remove(p); }
		}
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

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("PlayerNotifierConfig")) { return; }

		updateWhiteList();
	}

	private void updateWhiteList() {
		this.whitelist = config.whiteList().trim().split("\\s*,\\s*");
	}
}