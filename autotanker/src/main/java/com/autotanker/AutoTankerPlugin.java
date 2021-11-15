package com.autotanker;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
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
	name = "[w] Auto Tanker",
	description = "Auto Tanker for F2P.",
	tags = {"tank", "pizza", "f2p"},
	enabledByDefault = false
)
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

	final String PIZZA = "Anchovy pizza";
	final int PIZZA_ID = 2297;
	final String PIZZA_HALF= "1/2 anchovy pizza";
	final int PIZZA_HALF_ID = 2299;

	@Override
	protected void startUp()
	{

	}

	@Override
	protected void shutDown()
	{

	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		if (config.eatPizza()) {
			this.eatPizza();
		}

		if (config.combinePizza()) {
			this.combinePizza();
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
				// Get the menu entry to eat the pizza and invoke
				MenuEntry entry = getConsumableEntry(name, item.getId(), item.getIndex());
				clientThread.invoke(() ->
						client.invokeMenuAction(
								entry.getOption(),
								entry.getTarget(),
								entry.getIdentifier(),
								entry.getOpcode(),
								entry.getParam0(),
								entry.getParam1()
						)
				);
				// Return after eating 1 pizza this tick
				return;
			}
		}
	}

	private MenuEntry getConsumableEntry(String itemName, int itemId, int itemIndex) {
		return new MenuEntry("Eat",
				"<col=ff9040>" + itemName,
				itemId,
				MenuAction.ITEM_FIRST_OPTION.getId(),
				itemIndex,
				WidgetInfo.INVENTORY.getId(),
				false);
	}

	private void combinePizza() {
		// Get inventory widget
		Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

		// Inventory is null or bank interface is open
		if (inventory == null || client.getItemContainer(InventoryID.BANK) != null) { return; }

		clientThread.invoke(() -> {

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

			client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
			client.setSelectedItemSlot(firstPizza.getIndex());
			client.setSelectedItemID(firstPizza.getId());
			MenuEntry combineEntry = this.getCombineEntry(PIZZA_HALF, PIZZA_HALF_ID, lastPizza.getIndex());
			client.invokeMenuAction(
					combineEntry.getOption(),
					combineEntry.getTarget(),
					combineEntry.getIdentifier(),
					combineEntry.getOpcode(),
					combineEntry.getParam0(),
					combineEntry.getParam1()
			);
		});
	}

	private MenuEntry getCombineEntry(String itemName, int itemId, int itemIndex) {
		return new MenuEntry(
				"Use",
				"<col=ff9040>" + itemName + "<col=ffffff> -> <col=ff9040>" + itemName,
				itemId,
				MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
				itemIndex,
				WidgetInfo.INVENTORY.getId(),
				false);
	}

}