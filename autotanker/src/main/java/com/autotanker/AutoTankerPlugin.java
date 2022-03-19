package com.autotanker;

import com.google.inject.Provides;
import javax.inject.Inject;

import com.openosrs.client.util.PvPUtil;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerCompositionChanged;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.kit.KitType;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.Objects;
import java.util.stream.Stream;

@Extension
@PluginDescriptor(
	name = "[w] Auto Tanker",
	description = "Auto Tanker for F2P.",
	tags = {"tank", "pizza", "f2p"},
	enabledByDefault = false
)
@Slf4j
public class AutoTankerPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private AutoTankerConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	// Provides our config
	@Provides
	AutoTankerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AutoTankerConfig.class);
	}

	final static String PIZZA = "Anchovy pizza";
	final static int PIZZA_ID = 2297;
	final static String PIZZA_HALF= "1/2 anchovy pizza";
	final static int PIZZA_HALF_ID = 2299;

	final static int[] RANGED_WEAPONS = {ItemID.MAPLE_SHORTBOW, ItemID.MAPLE_LONGBOW};
	final static int[] MELEE_WEAPONS = {ItemID.RUNE_SCIMITAR, ItemID.RUNE_BATTLEAXE, ItemID.RUNE_2H_SWORD, ItemID.HILL_GIANT_CLUB};
	final static int[] MAGIC_WEAPONS = {ItemID.STAFF_OF_AIR, ItemID.STAFF_OF_EARTH, ItemID.STAFF_OF_FIRE, ItemID.STAFF_OF_WATER};

	private static final int CLAN_WARS_WIDGET = 5767170; // clan wars score widget

	private int ranged;
	private int melee;
	private int magic;

	@Override
	protected void startUp() {
		//initWeaponCounts();
	}

	@Override
	protected void shutDown() { }

	private boolean isF2P() {
		Stream<WorldType> var10000 = client.getWorldType().stream();
		EnumSet<WorldType> var10001 = EnumSet.of(WorldType.MEMBERS);
		Objects.requireNonNull(var10001);
		return var10000.anyMatch(var10001::contains);
	}

	private boolean isCWA() {
		final Widget clanWarsWidget = client.getWidget(CLAN_WARS_WIDGET);

		return clanWarsWidget != null && !clanWarsWidget.isHidden();
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		if (config.onlyF2P() && !isF2P()) { return; }
		if (config.onlyCWA() && !isCWA()) { return; }

		final String selectedName = client.getSelectedSpellName();
		final int selectedWidget = client.getSelectedSpellWidget();
		final int selectedChild = client.getSelectedSpellChildIndex();
		final boolean isSelected = client.getSpellSelected();

		if (config.eatPizza()) {
			this.eatPizza();
		}

		if (config.combinePizza()) {
			this.combinePizza();
		}

		if (config.swapPrayer()) {
			this.swapPrayer();
		}

		if (isSelected) { // there was a spell selected
			clientThread.invoke(() -> {
				client.setSelectedSpellWidget(selectedWidget); // reselect the spell after doing this ticks actions
				client.setSelectedSpellChildIndex(selectedChild);
				client.setSelectedSpellName(selectedName);
				client.setSpellSelected(true);
			});
		}

	}

	private void eatPizza() {
		// Get current player HP
		int health = this.client.getBoostedSkillLevel(Skill.HITPOINTS);

		// If HP is below threshold to eat pizza
		if (health > config.hpThresh()) { return; }

		// Get inventory widget
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		// Inventory is null or bank interface is open
		if (inventory == null || client.getItemContainer(InventoryID.BANK) != null) { return; }

		//For every item in the Inventory
		for (WidgetItem item : inventory.getWidgetItems()) {
			// Get item name
			final String name = this.itemManager.getItemComposition(item.getId()).getName();
			// Ff the item is PIZZA
			if (name.equals(PIZZA)) {
				clientThread.invoke(() ->
						client.invokeMenuAction(
								"Eat",
								"<col=ff9040>" + PIZZA,
								PIZZA_ID,
								MenuAction.ITEM_FIRST_OPTION.getId(),
								item.getIndex(),
								WidgetInfo.INVENTORY.getId()
						)
				);
				// Return after eating 1 pizza this tick
				return;
			}
		}
	}

	private void combinePizza() {
		// Get inventory widget
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		// Inventory is null or bank interface is open
		if (inventory == null || client.getItemContainer(InventoryID.BANK) != null) { return; }

		// Perform query for pizza half WidgetItems
		QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
				.idEquals(PIZZA_HALF_ID)
				.result(client);

		// If Query fails or no halves are found or there is only one half
		if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty() || widgetItemQueryResults.size() < 2) {
			return;
		}

		// Get the first and last pizza half from the query
		WidgetItem firstPizza = widgetItemQueryResults.first();
		WidgetItem lastPizza = widgetItemQueryResults.last();

		if (firstPizza == null || lastPizza == null) {
			return;
		}

		clientThread.invoke(() -> {
			client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
			client.setSelectedItemSlot(firstPizza.getIndex());
			client.setSelectedItemID(firstPizza.getId());
			client.invokeMenuAction(
					"Use",
					"<col=ff9040>" + PIZZA_HALF + "<col=ffffff> -> <col=ff9040>" + PIZZA_HALF,
					PIZZA_HALF_ID,
					MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
					lastPizza.getIndex(),
					WidgetInfo.INVENTORY.getId()
			);
		});
	}

	private void swapPrayer() {
		if (client.getLocalPlayer() == null) { return; }

		initWeaponCounts();

		HeadIcon current = client.getLocalPlayer().getOverheadIcon();
		if (current == null) {
			return; // Player is not praying, player must first pray before auto prayers begin switching. Prevents prayer waste/obviousness.
		}

		String prayerName;
		int prayerWidgetId;

		if (melee >= magic && melee >= ranged) { //prioritize melee if equal
			if (current == HeadIcon.MELEE) { return; }
			prayerName = Prayer.PROTECT_FROM_MELEE.name();
			prayerWidgetId = Prayer.PROTECT_FROM_MELEE.getWidgetInfo().getId();
		} else if (ranged >= magic && ranged >= melee) {
			if (current == HeadIcon.RANGED) { return; }
			prayerName = Prayer.PROTECT_FROM_MISSILES.name();
			prayerWidgetId = Prayer.PROTECT_FROM_MISSILES.getWidgetInfo().getId();
		} else {
			if (current == HeadIcon.MAGIC) { return; }
			prayerName = Prayer.PROTECT_FROM_MAGIC.name();
			prayerWidgetId = Prayer.PROTECT_FROM_MAGIC.getWidgetInfo().getId();
		}

		if (client.getWidget(prayerWidgetId) == null) { return; } // failsafe

		clientThread.invoke(() -> {
			client.invokeMenuAction(
					"Activate",
					prayerName,
					1,
					MenuAction.CC_OP.getId(),
					-1,
					prayerWidgetId
			);
		});
	}

	private void initWeaponCounts() {
		ranged = 0;
		melee = 0;
		magic = 0;

		for (Player p : client.getPlayers()) {
			if (p == null || p.getPlayerComposition() == null) { continue; }

			if (playerInTeam(p)) { continue; }

			final int weapon = p.getPlayerComposition().getEquipmentId(KitType.WEAPON);

			if (inArray(RANGED_WEAPONS, weapon)) {
				ranged++;
			} else if (inArray(MAGIC_WEAPONS, weapon)) {
				magic++;
			} else if (inArray(MELEE_WEAPONS, weapon)) {
				melee++;
			}
		}
	}

	//@Subscribe
	private void onPlayerSpawned(PlayerSpawned event) {
		if (event == null || event.getPlayer() == null || event.getPlayer().getPlayerComposition() == null) { return; }

		if (playerInTeam(event.getPlayer())) { return; }

		final int weapon = event.getPlayer().getPlayerComposition().getEquipmentId(KitType.WEAPON);

		if (inArray(RANGED_WEAPONS, weapon)) {
			ranged++;
		} else if (inArray(MAGIC_WEAPONS, weapon)) {
			magic++;
		} else if (inArray(MELEE_WEAPONS, weapon)) {
			melee++;
		}
	}

	//@Subscribe
	private void onPlayerCompositionChanged(PlayerCompositionChanged event) {
		if (event == null || event.getPlayer() == null || event.getPlayer().getPlayerComposition() == null) { return; }


	}

	private boolean playerInTeam(Player player) {
		if (player == client.getLocalPlayer() || player == null || client.getLocalPlayer() == null) { return true; }

		boolean cape = client.getLocalPlayer().getTeam() == player.getTeam();

		boolean cc = playerInFriendsChat(player.getName());

		if (config.teamMode() == TeamMode.CC) {
			return cc;
		} else if (config.teamMode() == TeamMode.CAPE) {
			return cape;
		} else {
			return cc || cape;
		}
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

	private String correctName(String name) { // the fc player has the wrong char code for a space compared to the other name???
		byte[] bytes = name.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i<bytes.length; i++) {
			if (bytes[i] == 63) {
				bytes[i] = 32;
			}
		}
		return new String(bytes);
	}

	private boolean inArray(int[] arr, int val) {
		for (int x: arr) {
			if (x == val) { return true; }
		}
		return false;
	}
}