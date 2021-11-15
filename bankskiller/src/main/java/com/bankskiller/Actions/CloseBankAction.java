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
public class CloseBankAction implements IAction {
    public CloseBankAction() {

    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        log.info("CloseBankAction START");
        clientThread.invoke(() -> {
            // AClose bank
            client.invokeMenuAction(
                    "Close",
                    "",
                    1,
                    MenuAction.CC_OP.getId(),
                    11,
                    786434 // 17694736 // Close bank widget is undefined in WidgetInfo
            );
        });
        log.info("CloseBankAction SUCCEEDED");
    }
}
