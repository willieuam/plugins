package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class UseItemAction implements IAction {
    private final int item1;
    private final String name1;
    private final int item2;
    private final String name2;

    public UseItemAction(int item1, String name1, int item2, String name2) {
        this.name1 = name1;
        this.item1 = item1;
        this.name2 = name2;
        this.item2 = item2;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        log.info("UseItemAction START");
        // Get inventory widget
        Widget inventory = client.getWidget(WidgetInfo.INVENTORY);

        // Inventory is null or bank interface is open
        if (inventory == null) { return; }

        clientThread.invoke(() -> {

            // Perform query for first item WidgetItems
            QueryResults<WidgetItem> widgetItem1QueryResults = new InventoryWidgetItemQuery()
                    .idEquals(item1)
                    .result(client);

            // Perform query for second item WidgetItems
            QueryResults<WidgetItem> widgetItem2QueryResults = new InventoryWidgetItemQuery()
                    .idEquals(item2)
                    .result(client);


            // If Query fails or no items are found
            if (widgetItem1QueryResults == null || widgetItem2QueryResults == null ||
                    widgetItem1QueryResults.isEmpty() || widgetItem2QueryResults.isEmpty()){
                return;
            }

            WidgetItem widgetItem1 = widgetItem1QueryResults.first();
            WidgetItem widgetItem2;
            if (item1 == item2) {
                widgetItem2 = widgetItem2QueryResults.last();
            } else {
                widgetItem2 = widgetItem2QueryResults.first();
            }

            if (widgetItem1 == null || widgetItem2 == null) {
                return;
            }

            if (widgetItem1.equals(widgetItem2)) {
                return;
            }

            client.setSelectedItemWidget(WidgetInfo.INVENTORY.getId());
            client.setSelectedItemSlot(widgetItem1.getIndex());
            client.setSelectedItemID(widgetItem1.getId());
            client.invokeMenuAction(
                    "Use",
                    "<col=ff9040>" + name1 + "<col=ffffff> -> <col=ff9040>" + name2,
                    item2,
                    MenuAction.ITEM_USE_ON_WIDGET_ITEM.getId(),
                    widgetItem2.getIndex(),
                    WidgetInfo.INVENTORY.getId()
            );
        });
        log.info("UseItemAction SUCCEEDED");
    }
}
