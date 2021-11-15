package com.bankskiller.Actions;

import com.bankskiller.BankSkillerPlugin;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;

public interface IAction {
    public void doAction(BankSkillerPlugin plugin, Client client, ClientThread clientThread);
}
