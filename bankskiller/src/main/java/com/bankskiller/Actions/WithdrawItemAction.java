package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.QueryResults;
import net.runelite.api.queries.BankItemQuery;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class WithdrawItemAction implements IAction {
    private final int item;
    private final String name;
    private final String count;

    public WithdrawItemAction(int item, String name, String count) {
        this.item = item;
        this.name = name;
        this.count = count;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        log.info("WithdrawItemAction START");

        QueryResults<WidgetItem> widgetItemQueryResults = new BankItemQuery()
                .idEquals(item)
                .result(client);

        // If Query fails or no items with that id are found
        if (widgetItemQueryResults == null || widgetItemQueryResults.isEmpty()) { return; }
        WidgetItem widgetItem = widgetItemQueryResults.first();
        log.info(widgetItemQueryResults.toString());

        //WidgetItem widgetItem = widgetItemQueryResults.first();
        if (widgetItem == null) { return; }
        clientThread.invoke(() -> {
            // Deposit all option on the item
            client.invokeMenuAction(
                    "Withdraw-" + count,
                    "<col=ff9040>" + name,
                    this.withdrawMenuEntryId(),
                    MenuAction.CC_OP_LOW_PRIORITY.getId(),
                    widgetItem.getIndex(),
                    WidgetInfo.BANK_ITEM_CONTAINER.getId()
            );
        });
        log.info("WithdrawItemAction SUCCEEDED");
    }

    // Doing a Withdraw from the bank ties the Id in the MenuAction to a code that is not given anywhere
    // Right click-Withdrawing the set "X" amount has a code of 5.
    private int withdrawMenuEntryId() {
        switch (count) {
            case "1":
                return 1;
            case "5":
                return 3;
            case "10":
                return 4;
            case "All":
                return 7;
            default:
                return 5;
        }
    }
}
