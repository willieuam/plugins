package com.autocaster;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.openosrs.client.game.SoundManager;
import com.openosrs.client.util.PvPUtil;
import lombok.AccessLevel;
import lombok.Getter;
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
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.lang.Math.abs;

@Extension
@PluginDescriptor(
	name = "[w] Auto Caster",
	description = "Automatically cast spells on targets around you.",
	tags = {"player", "spell", "pk", "clan", "pvp"},
	enabledByDefault = false
)
public class AutoCasterPlugin extends Plugin {
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private AutoCasterConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	// Provides our config
	@Provides
	AutoCasterConfig provideConfig(ConfigManager configManager) { return configManager.getConfig(AutoCasterConfig.class); }

	@Getter(AccessLevel.PACKAGE)
	private boolean enabled;
	private boolean isCastTick;
	private boolean isWalkTick;
	private boolean isSecondCastTick;
	private Player previousTarget;
	private String[] whitelist;
	private String[] targetList;
	private Map<String, Integer> cache;
	private int delay;
	private static final int cacheDuration = 250; // 250 ticks = 2:30
	private static final int COMBAT_SPELL_DELAY = 5;

	private static final int CLAN_WARS_WIDGET = 5767170; // clan wars score widget

	private AutoCasterEnabledInfoBox enabledInfoBox;

	@Override
	protected void startUp() {
		this.updateWhiteList();
		this.updateTargetList();
		this.cache = new HashMap<>();

		enabled = false;
		isCastTick = false;
		isWalkTick = false;
		isSecondCastTick = false;
		delay = 0;

		previousTarget = null;

		keyManager.registerKeyListener(toggleKeyBindListener);
		keyManager.registerKeyListener(castKeyBindListener);

		enabledInfoBox = new AutoCasterEnabledInfoBox(itemManager.getImage(ItemID.MAGIC_STAFF), this);
		infoBoxManager.addInfoBox(enabledInfoBox);
	}

	@Override
	protected void shutDown() {
		this.whitelist = null;
		this.targetList = null;
		this.cache = null;

		enabled = false;
		isCastTick = false;
		isWalkTick = false;
		isSecondCastTick = false;
		delay = 0;

		previousTarget = null;

		keyManager.unregisterKeyListener(toggleKeyBindListener);
		keyManager.unregisterKeyListener(castKeyBindListener);

		infoBoxManager.removeInfoBox(enabledInfoBox);
		enabledInfoBox = null;
	}

	private final HotkeyListener toggleKeyBindListener = new HotkeyListener(() -> config.toggleKey()) {
		@Override
		public void hotkeyPressed()
		{
			enabled = !enabled;
		}
	};

	private final HotkeyListener castKeyBindListener = new HotkeyListener(() -> config.castKey()) {
		@Override
		public void hotkeyPressed()
		{
			cast();
		}
	};

	private boolean cast() {
		Player target = target();
		AutoAttack spell = spellFirst();

		boolean casted = autoAttackSpell(target, spell);

		if (casted) {
			if (config.enableCache()) {
				addPlayerToCache(target);
			}
			if (config.enableSecond() && !isSecondCastTick) {
				isSecondCastTick = true;
				previousTarget = target;
			}
		}

		return casted;
	}

	private void castSecond(Player target) {
		AutoAttack spell = spellSecond();

		autoAttackSpell(target, spell);

		isSecondCastTick = false;
		previousTarget = null;
	}

	@Subscribe
	protected void onGameTick(GameTick event) {
		if (isSecondCastTick) {
			castSecond(previousTarget);
		}

		tickCache();
		delay--;
		if (delay < 0) { delay = 0; }

		if (!enabled || delay != 0) { return; }

		boolean casted = cast();
		isCastTick = casted;

		if (casted) {
			delay = config.delay() == 0 ? COMBAT_SPELL_DELAY : config.delay();
		}
	}

	@Subscribe
	protected void onMenuOptionClicked(MenuOptionClicked event) {
		if (event == null) { return; }

		//if (!config.enableRecast()) { return; }

		// if we casted on this tick, and the action is interrupted by anything thats not walking, recast
		// if we walk at any point in a tick, we dont recast on that tick again as its implied we want the walk to be the action

		//isCastTick = false;

		//if (!isCastTick) {
		//	return;
		//}
		//
		//delay = 0;

		//if (event.getMenuOption().equals("Walk here")) {
		//	isWalkTick = true;
		//	delay = 0; // reset delay as we did not actually cast on this tick due to moving instead
		//}
		//
		//if (isWalkTick) {
		//	return;
		//}
		//
		//cast();
	}

	private AutoAttack spellFirst() {
		return spell(config.autoAttackType());
	}

	private AutoAttack spellSecond() {
		return spell(config.autoAttackTypeSecond());
	}

	private AutoAttack spell(AutoCasterType type) {
		switch (type) {
			case TB:
				if (spellAvailable(AutoAttack.TB)) {
					return AutoAttack.TB;
				}
				break;
			case SNARES_F2P:
				if (spellAvailable(AutoAttack.SNARE)) {
					return AutoAttack.SNARE;
				}
				break;
			case SNARES_P2P:
				if (spellAvailable(AutoAttack.ENTANGLE)) {
					return AutoAttack.ENTANGLE;
				}
				if (spellAvailable(AutoAttack.SNARE)) {
					return AutoAttack.SNARE;
				}
				break;
			case SINGLE_SPELLS:
				if (spellAvailable(AutoAttack.BLITZ)) {
					return AutoAttack.BLITZ;
				}
				if (spellAvailable(AutoAttack.RUSH)) {
					return AutoAttack.RUSH;
				}
				break;
			case MULTI_SPELLS:
				if (spellAvailable(AutoAttack.BARRAGE)) {
					return AutoAttack.BARRAGE;
				}
				if (spellAvailable(AutoAttack.BURST)) {
					return AutoAttack.BURST;
				}
				break;
		}
		return null;
	}

	private List<Player> targets() {
		List<Player> targets = new ArrayList<>();
		for (Player p : client.getPlayers()) {
			if (p != client.getLocalPlayer() &&
				!playerIsWhiteListed(p) &&
				!playerInCache(p) &&
				(!config.checkLevelRange() || PvPUtil.isAttackable(client, p) || inClanWarsArena())) {
						targets.add(p);
			}
		}
		return targets;
	}

	private Player target() {
		List<Player> targets = targets();

		if (config.enableTargetList()) {
			// check target list and return since that player should always be targeted first
			Player targetFromTargetList = fromTargetList(targets);
			if (targetFromTargetList != null) {
				return targetFromTargetList;
			}
		}

		switch (config.targetType()) {
			case CLOSEST:
				int closestDistance = 99999999;
				Player closest = null;
				Player localPlayer = client.getLocalPlayer();

				if (localPlayer == null) { return null; }

				for (Player p : targets) {
					int distance =
							abs(localPlayer.getLocalLocation().getSceneX() - p.getLocalLocation().getSceneX()) +
									abs(localPlayer.getLocalLocation().getSceneY() - p.getLocalLocation().getSceneY());
					if (distance == 0) { continue; } // ingore dded players incase frozen

					if (closest == null) {
						closestDistance = distance;
						closest = p;
						continue;
					}

					if (distance < closestDistance) {
						closestDistance = distance;
						closest = p;
					}
				}
				return closest;
			case RANDOM:
			default:
				try {
					Random r = new Random();
					return targets.get(r.nextInt(targets.size()));
				} catch (Exception e) { return null; }
		}
	}

	private Player fromTargetList(List<Player> targets) {
		for (Player p : targets) {
			for (String t : this.targetList) {
				if (p.getName() == null) { continue; }
				if (p.getName().equalsIgnoreCase(t) && !playerInCache(p)) {
					return p;
				}
			}
		}
		return null;
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

	private boolean spellAvailable(AutoAttack type) {
		final int VARBIT_SPELLBOOK = 4070;

		if (type.getLevel() > client.getBoostedSkillLevel(Skill.MAGIC)) { // magic level too low to cast
			return false;
		}

		final Widget widget = client.getWidget(type.getWidgetInfo());
		if (widget == null) { ;
			return false;
		}

		if (client.getVarbitValue(VARBIT_SPELLBOOK) != type.getSpellbook() ||
				widget.getSpriteId() == type.getDisabledSpriteId() ||
				widget.getSpriteId() != type.getEnabledSpriteId()) {
			return false;
		}
		return true;
	}

	private boolean autoAttackSpell(Player player, AutoAttack type) {
		if (player == null || type == null) { return false; }

		final Widget spell = client.getWidget(type.getWidgetInfo());
		if (spell == null) { return false; }
		this.clientThread.invoke(() -> {
			client.setSelectedSpellName("<col=00ff00>" + spell.getName() + "</col>");
			client.setSelectedSpellWidget(spell.getId());
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

		return true;
	}

	private boolean inClanWarsArena() {
		final Widget clanWarsWidget = client.getWidget(CLAN_WARS_WIDGET);

		return clanWarsWidget != null && !clanWarsWidget.isHidden();
	}

	private void addPlayerToCache(Player player) {
		cache.put(Objects.requireNonNull(player.getName()).toLowerCase(), config.cacheDuration() == 0 ? cacheDuration : config.cacheDuration());
	}

	private boolean playerInCache(Player player) {
		return config.enableCache() && cache.containsKey(Objects.requireNonNull(player.getName()).toLowerCase());
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

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("autocaster")) { return; }

		updateWhiteList();
		updateTargetList();
	}

	private void updateTargetList() { this.targetList = config.targetList().trim().split("\\s*,\\s*"); }

	private void updateWhiteList() { this.whitelist = config.whiteList().trim().split("\\s*,\\s*"); }
}