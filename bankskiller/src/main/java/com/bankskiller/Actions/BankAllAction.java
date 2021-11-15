package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class BankAllAction implements IAction {
    public BankAllAction() {

    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        clientThread.invoke(() -> {
            log.info("BankAllAction START");
            // Access bank with Bank menu option
            client.invokeMenuAction(
                    "Deposit inventory",
                    "",
                    1,
                    MenuAction.CC_OP.getId(),
                    -1,
                    WidgetInfo.BANK_DEPOSIT_INVENTORY.getId()
            );
        });
        log.info("BankAllAction SUCCEEDED");
    }
}
