package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

public class WaitAction implements IAction {
    private final int ticks;

    public WaitAction(int ticks) {
        this.ticks = ticks;
    }

    @Override
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread) {
        plugin.delay += this.ticks;
    }
}
