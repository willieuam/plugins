package com.wpneck;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
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

@Extension
@PluginDescriptor(
	name = "[w] Pneck",
	description = "Equip pnecks automatically",
	tags = {"phoenix", "necklace", "tank"},
	enabledByDefault = false
)
public class wPneckPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private wPneckConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	// Provides our config
	@Provides
	wPneckConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(wPneckConfig.class);
	}

	private static final String PNECK_NAME = "Phoenix necklace";
	private static final int PNECK_ID = 11090;
	private static final String PNECK_BREAK = "Your phoenix necklace heals you, but is destroyed in the process.";

	@Override
	protected void startUp() { }

	@Override
	protected void shutDown() { }

	@Subscribe
	private void onGameTick(GameTick gameTick) {
		if (config.mode() == wPneckMode.ONTICK) {
			int worn = wornNeck();
			// wearing pneck or no amulet (pneck broke)
			if (worn == PNECK_ID || worn == -1) {
				equipPneck();
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (chatMessage.getMessage().contains(PNECK_BREAK)) {
			equipPneck();
		}
	}

	private int wornNeck() {
		Player lp = client.getLocalPlayer();
		if (lp == null) { return 0; }

		PlayerComposition lpc = lp.getPlayerComposition();
		if (lpc == null) { return 0; }

		return lpc.getEquipmentId(KitType.AMULET);
	}

	private void equipPneck() {
		// Get inventory widget
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		// Inventory is null or bank interface is open
		if (inventory == null || client.getItemContainer(InventoryID.BANK) != null) { return; }

		//For every item in the Inventory
		for (WidgetItem item : inventory.getWidgetItems()) {
			if (item.getId() == PNECK_ID) {
				clientThread.invoke(() ->
						client.invokeMenuAction(
								"Wear",
								"Wear",
								PNECK_ID,
								MenuAction.ITEM_SECOND_OPTION.getId(),
								item.getIndex(),
								WidgetInfo.INVENTORY.getId()
						)
				);
				return; // return after equipping
			}
		}
		// no pnecks found to equip, do nothing
	}
}