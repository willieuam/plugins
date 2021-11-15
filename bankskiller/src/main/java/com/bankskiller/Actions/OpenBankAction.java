package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.InventoryID;
import net.runelite.api.MenuAction;
import net.runelite.api.queries.GameObjectQuery;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;

@Slf4j
public class OpenBankAction implements IAction {
    private final String bankMenuOption;
    private final String bankObjectName;

    public OpenBankAction(String bankObjectName, String bankMenuOption) {
        this.bankObjectName = bankObjectName;
        this.bankMenuOption = bankMenuOption;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        log.info("OpenBankAction START");
        if (client.getLocalPlayer() == null) {
            log.info("client.getLocalPlayer() == null");
            return;
        }

        log.info("bankObjectName " + bankObjectName);
        log.info("bankMenuOption " + bankMenuOption);

        // Get the bank object nearest to the player and access it
        GameObject bankObject =
                new GameObjectQuery()
                        .nameEquals(bankObjectName)
                        .result(client)
                        .nearestTo(client.getLocalPlayer());

        // There is no bank object nearby
        if (bankObject == null) {
            log.info("bankObject == null");
            return;
        }

        clientThread.invoke(() -> {
            // Access bank with Bank menu option
            client.invokeMenuAction(
                    bankMenuOption,
                    "<col=ffff>" + bankObjectName,
                    bankObject.getId(),
                    getActionId(),
                    bankObject.getSceneMinLocation().getX(),
                    bankObject.getSceneMinLocation().getY()
            );
        });
        log.info("OpenBankAction SUCCEEDED");
    }

    private int getActionId() {
        switch (bankMenuOption) {
            case "Use":
                return MenuAction.GAME_OBJECT_FIRST_OPTION.getId();
            case "Bank":
                return MenuAction.GAME_OBJECT_SECOND_OPTION.getId();
            default:
                return MenuAction.GAME_OBJECT_SECOND_OPTION.getId();
        }
    }

}
