package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.MenuAction;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;

public class ProcessScreenAction implements IAction {
    private final String name;
    private final String action;
    private final int offset;
    private final int base = 17694734;  // Base ID for MultiSkill menu, increment by 1 for every box you move right

    public ProcessScreenAction(String action, String name, int offset) {
        this.action = action;
        this.name = name;
        this.offset = offset;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        clientThread.invoke(() -> {
            // Access bank with Bank menu option
            client.invokeMenuAction(
                    action,
                    "<col=ff9040>" + name,
                    1,
                    MenuAction.CC_OP.getId(),
                    -1,
                    base + offset
            );
        });
    }
}
