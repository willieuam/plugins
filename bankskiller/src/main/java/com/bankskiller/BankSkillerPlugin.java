package com.bankskiller;

import com.bankskiller.Actions.*;
import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.ConfigButtonClicked;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDescriptor(
    name = "[w] Bank Auto Skiller",
    description = "Automatically do bank skilling activities.",
    tags = {"skill", "auto", "bank"},
    enabledByDefault = false
)
@Slf4j
public class BankSkillerPlugin extends Plugin {
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private BankSkillerConfig config;

    @Provides
    BankSkillerConfig provideConfig(final ConfigManager configManager) {
        return configManager.getConfig(BankSkillerConfig.class);
    }

    private boolean run;
    private IAction[] actions;
    private int action;
    public int delay;

    @Override
    protected void startUp() {
        this.reset();
    }

    @Override
    protected void shutDown() {
        this.reset();
    }

    private void reset() {
        log.info("RESET" + config.actionsScript());
        run = false;
        actions = parseActionsScript(config.actionsScript());
        action = 0;
        delay = 0;
    }

    @Subscribe
    protected void onGameTick(final GameTick event) {
        // If plugin is not running, return
        if (!run) { return; }
        // If there is currently a delay, return
        if (delay > 0) {
            log.info("SKIPPING" + String.valueOf(delay));
            delay--;
            return;
        }
        if (actions.length == 0) { return; }

        IAction currentAction = actions[action];
        log.info("DOING" + String.valueOf(action));
        currentAction.doAction(this, this.client, this.clientThread);
        action++;

        // After every action, randomly roll the tick delay before doing the next action
        delay += randomDelay();
        log.info("RANDOM DELAY" + String.valueOf(delay));

        // If that was the last action in the sequence, randomly decide to take a break and
        // Reset action index to 0
        if (action == actions.length) {
            log.info("REACHED END OF CYCLE");
            action = 0;
            if (takeBreak()) { delay += randomBreak(); }
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        if (event == null) { return; }
        log.info("---------------------------------------------------");
        log.info(event.toString());
        log.info(event.getMenuOption());
        log.info(event.getMenuTarget());
        log.info(String.valueOf(event.getId()));
        log.info(event.getMenuAction().toString());
        log.info(String.valueOf(event.getParam0()));
        log.info(String.valueOf(event.getParam1()));
        log.info("---------------------------------------------------");
    }

    @Subscribe
    protected void onConfigButtonClicked(ConfigButtonClicked event) {
        // If the config clicked is a different config
        if (!event.getGroup().equals(BankSkillerConfig.class.getAnnotation(ConfigGroup.class).value())) {
            return;
        }

        switch (event.getKey()) {
            // Run script
            case "startButton":
                run = true;
                break;
            // Pause script
            case "stopButton":
                run = false;
                break;
            // Reset (as if disabling and enabling)
            case "resetButton":
                this.reset();
                break;
        }
    }

    // Get a random amount of ticks to delay action for based on config
    private int randomDelay() {
        return randomWait(config.minDelay(), config.maxDelay());
    }

    private boolean takeBreak() {
        return Math.random() < config.breakChance();
    }

    // Get a random amount of ticks to take a break for based on config
    private int randomBreak() {
        return randomWait(config.minBreak(), config.maxBreak());
    }

    // Generate a random amount of ticks of at least min and at most max
    private int randomWait(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    private IAction[] parseActionsScript(String actionsScript) {
        String[] actionsStringArr = actionsScript.split("\\r?\\n");
        IAction[] actionsArr = new IAction[actionsStringArr.length];

        for (int n = 0; n < actionsStringArr.length; n++) {
            String actionString = actionsStringArr[n];
            // Split actionString into parts divided by , with no trailing whitespaces
            String[] actionStringArr = actionString.trim().split("\\s*,\\s*");

            try {
                actionString = actionStringArr[0];
                final int id;
                final String name;
                final int id2;
                final String name2;
                switch (actionString) {
                    case "OpenBank":
                        // Create OpenBankAction with first argument as Bank Object Name
                        // OpenBank, Bank booth, Bank
                        actionsArr[n] = new OpenBankAction(actionStringArr[1], actionStringArr[2]);
                        break;
                    case "BankAll":
                        // Create BankAllAction
                        // BankAll
                        actionsArr[n] = new BankAllAction();
                        break;
                    case "BankItem":
                        // Create BankItemAction
                        // BankItem, 1603
                        id = Integer.parseInt(actionStringArr[1]);
                        name = itemManager.getItemComposition(id).getName();
                        actionsArr[n] = new BankItemAction(id, name);
                        break;
                    case "WithdrawItem":
                        // Create WithdrawItemAction
                        // WithdrawItem, 1619, All
                        // WithdrawItem, 1619, 1
                        // WithdrawItem, 1619, 7
                        id = Integer.parseInt(actionStringArr[1]);
                        name = itemManager.getItemComposition(id).getName();
                        actionsArr[n] = new WithdrawItemAction(id, name, actionStringArr[2]);
                        break;
                    case "UseItem":
                        // Create UseItemAction
                        // UseItem, 1755, 1619
                        id = Integer.parseInt(actionStringArr[1]);
                        name = itemManager.getItemComposition(id).getName();
                        id2 =Integer.parseInt(actionStringArr[2]);
                        name2 = itemManager.getItemComposition(id2).getName();
                        actionsArr[n] = new UseItemAction(id, name, id2, name2);
                        break;
                    case "ProcessScreen":
                        // Accept the confirmation in the chat box
                        // ProcessScreen, Cut, 1619, 0
                        // ProcessScreen, Action Name, Item ID, Multiskill Interface Offset
                        id = Integer.parseInt(actionStringArr[2]);
                        name = itemManager.getItemComposition(id).getName();
                        actionsArr[n] = new ProcessScreenAction(actionStringArr[1], name, Integer.parseInt(actionStringArr[3]));
                        break;
                    case "CloseBank":
                        // Close the bank interface
                        // CloseBank
                        actionsArr[n] = new CloseBankAction();
                        break;
                    case "Wait":
                        // Wait for x ticks (eg, 55 ticks to fully cut the inventory of rubies)
                        // Wait, 55
                        actionsArr[n] = new WaitAction(Integer.parseInt(actionStringArr[1]));
                        break;
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException exception) {
                log.info(exception.getMessage());
            }
        }
        return actionsArr;
    }
}
