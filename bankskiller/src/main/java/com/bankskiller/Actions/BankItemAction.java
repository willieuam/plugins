package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.QueryResults;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class BankItemAction implements IAction {
    private final int item;
    private final String name;

    public BankItemAction(int item, String name) {
        this.item = item;
        this.name = name;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        log.info("BankItemAction START");
        clientThread.invoke(() -> {
            QueryResults<WidgetItem> widgetItemQueryResults = new InventoryWidgetItemQuery()
                    .idEquals(item)
                    .result(client);

            // If Query fails or no items with that id are found
            if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) { return; }

            WidgetItem widgetItem = widgetItemQueryResults.first();
            if (widgetItem == null) { return; }

            // Deposit all option on the item
            client.invokeMenuAction(
                    "Deposit-All",
                    "<col=ff9040>" + name,
                    8, // Deposit-All MenuEntry has the id of 8
                    MenuAction.CC_OP_LOW_PRIORITY.getId(),
                    widgetItem.getIndex(),
                    WidgetInfo.BANK_INVENTORY_ITEMS_CONTAINER.getId()
            );
        });
        log.info("BankItemAction SUCCEEDED");
    }
}
