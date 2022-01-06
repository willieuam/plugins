package com.waerialfishing;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
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
	name = "Aerial Fishing",
	description = "Aerial Fishing",
	tags = {"fish"},
	enabledByDefault = false
)
public class wAerialFishingPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	private final int knife = 946;
	private final int[] fishes = new int[] {22826, 22829, 22832, 22835};

	@Override
	protected void startUp() { }

	@Override
	protected void shutDown() { }

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{

	}

	private MenuEntry getCutEntry(String itemName, int itemId, int itemIndex) {
		return client.createMenuEntry(
				"Use",
				"<col=ff9040>Knife<col=ffffff> -> <col=ff9040>" + itemName,
				itemId,
				MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
				itemIndex,
				WidgetInfo.INVENTORY.getId(),
				true
		);
	}

	private WidgetItem lastItem(int id) {
		QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
				.idEquals(id)
				.result(client);
		if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) {
			return null;
		}
		return widgetItemQueryResults.last();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		if (!(event.getMenuTarget().equals("Use") && event.getMenuOption().equals("Use") &&
			event.getMenuAction() == MenuAction.ITEM_USE && event.getId() == knife &&
			event.getParam1() == WidgetInfo.INVENTORY.getId())) {
			return;
		}

		// Get inventory widget
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		// Inventory is null or bank interface is open
		if (inventory == null || client.getItemContainer(InventoryID.BANK) != null) { return; }

		WidgetItem last = null;
		for (int id : this.fishes) {
			last = lastItem(id);
			if (last != null) { break; }
		}
		if (last == null) { return; }

		WidgetItem finalLast = last;
		clientThread.invoke(() -> {

			client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
			client.setSelectedItemSlot(event.getParam0());
			client.setSelectedItemID(knife);
			final MenuEntry cutEntry = this.getCutEntry(itemManager.getItemComposition(finalLast.getId()).getName(), finalLast.getId(), finalLast.getIndex());
			//client.invokeMenuAction(
			//		cutEntry.getOption(),
			//		cutEntry.getTarget(),
			//		cutEntry.getIdentifier(),
			//		cutEntry.getType().getId(),
			//		cutEntry.getParam0(),
			//		cutEntry.getParam1()
			//);
		});
	}
}