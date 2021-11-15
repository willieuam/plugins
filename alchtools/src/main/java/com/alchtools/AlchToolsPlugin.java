package com.alchtools;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOpened;
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
	name = "[w] Alch Tools",
	description = "Alch Tools",
	tags = {"alch", "auto"},
	enabledByDefault = false
)
@Slf4j
public class AlchToolsPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private AlchToolsConfig config;

	@Inject
	private ClientThread clientThread;

	@Inject
	private ItemManager itemManager;

	// Provides our config
	@Provides
	AlchToolsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AlchToolsConfig.class);
	}

	protected Integer[] items;

	@Override
	protected void startUp() {
		final String itemsStr = config.items();
		final String[] itemsStrArr = itemsStr.trim().split("\\s*,\\s*");

		items = new Integer[itemsStrArr.length];

		for (int n=0;n<itemsStrArr.length;n++) {
			try {
				items[n] = Integer.parseInt(itemsStrArr[n]);
			} catch (NumberFormatException nfe) {
				items[n] = null;
			}
		}

		log.info("ITEMS:");
		for (Integer i : items) {
			log.info(i.toString());
		}
	}

	@Override
	protected void shutDown() {

	}

	private MenuEntry alchMenuEntry(int itemIndex, int itemId, String itemName) {
		log.info("alchMenuEntry:" + String.valueOf(itemId) + " " + String.valueOf(itemIndex) + " " + itemName);
		return new MenuEntry(
				"Cast",
				"<col=00ff00>High Level Alchemy</col><col=ffffff> -> <col=ff9040>" + itemName,
				itemId,
				MenuAction.ITEM_USE_ON_WIDGET.getId(),
				itemIndex,
				WidgetInfo.INVENTORY.getId(),
				true
		);
	}

	private void addMenuEntry(MenuOpened event, MenuEntry alchMenuEntry) {
		final MenuEntry[] menuList = new MenuEntry[event.getMenuEntries().length + 1];

		// [entry1, entry2, entry3] -> [entry1, null, entry2, entry3]
		for (int i = event.getMenuEntries().length - 1; i >= 0; i--)
		{
			if (i == 0) { menuList[i] = event.getMenuEntries()[i]; }
			else { menuList[i + 1] = event.getMenuEntries()[i]; }
		}
		// [entry1, null, entry2, entry3] -> [entry1, alchMenuEntry, entry2, entry3]
		menuList[1] = alchMenuEntry;

		log.info("menuList");
		for (MenuEntry entry : menuList) {
			log.info(entry.toString());
		}
		event.setMenuEntries(menuList);
		event.setModified();

		client.setLeftClickMenuEntry(alchMenuEntry);
		log.info("MenuOpened: " + event.toString());
	}

	@Subscribe
	protected void onMenuOpened(MenuOpened event) {
		if (client == null || event == null) { return; }

		final MenuEntry entry = event.getFirstEntry();
		if (entry == null) { return; }

		final int itemId = entry.getIdentifier();
		if (itemId == -1) { return; }

		log.info("itemId: " + String.valueOf(itemId));

		if (inItems(itemId)) {
			log.info("itemId in items");
			final String itemName = itemManager.getItemComposition(itemId).getName();
			log.info("itemName: " + itemName);
			final int itemIndex = entry.getParam0();
			log.info("itemIndex: " + String.valueOf(itemIndex));
			addMenuEntry(event, alchMenuEntry(itemIndex, itemId, itemName));
		}
	}

	@Subscribe
	protected void onMenuOptionClicked(MenuOptionClicked event) {
		log.info("onMenuOptionClicked");
		if (isAlchMenuEntry(event)) {
			log.info("onMenuOptionClicked: isAlchMenuEntry");
			client.setSelectedSpellName("<col=00ff00>High Level Alchemy</col><col=ffffff>");
			client.setSelectedSpellWidget(WidgetInfo.SPELL_HIGH_LEVEL_ALCHEMY.getId());
			// something to reset view to inventory
		}
	}

	private boolean inItems(final int itemId) {
		if (items == null) { return false; }
		for(int i : items) {
			if (i == itemId) {
				return true;
			}
		}
		return false;
	}

	private boolean isAlchMenuEntry(MenuOptionClicked event) {
		return (event.getMenuOption().equals("Cast") && event.getMenuAction() == MenuAction.ITEM_USE_ON_WIDGET &&
				event.getParam1() == WidgetInfo.INVENTORY.getId());
	}

	@Subscribe
	protected void onGameTick(GameTick gameTick) {

	}
}