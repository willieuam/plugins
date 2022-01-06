package com.infernoautopray.wautoprayutils;

import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
import net.runelite.api.MenuAction;
import net.runelite.client.callback.ClientThread;

public class wAutoPrayUtils {
	private final Client client;
	private final ClientThread clientThread;
	
	public wAutoPrayUtils(Client client, ClientThread clientThread) {
		this.client = client;
		this.clientThread = clientThread;
	}

	public Prayer currentPrayer() {
		assert client.getLocalPlayer() != null;
		HeadIcon icon = client.getLocalPlayer().getOverheadIcon();
		if (icon == null) { return Prayer.NONE; }

		switch (icon) {
			case MAGIC:
				return Prayer.MAGIC;
			case RANGED:
				return Prayer.RANGED;
			case MELEE:
				return Prayer.MELEE;
			default:
				return Prayer.NONE;
		}
	}

	public void activatePrayer(Prayer prayer) {
		if (this.currentPrayer() == prayer) { return; }

		this.clientThread.invoke(() -> {
			client.invokeMenuAction(
					"Activate",
					prayer.getName(),
					1,
					MenuAction.CC_OP.getId(),
					-1,
					prayer.getWidget().getId()
			);
		});
	}

	public void deactivatePrayer() {
		Prayer prayer = this.currentPrayer();

		if (prayer == Prayer.NONE) { return; }

		this.clientThread.invoke(() -> {
			client.invokeMenuAction(
					"Deactivate",
					prayer.getName(),
					1,
					MenuAction.CC_OP.getId(),
					-1,
					prayer.getWidget().getId()
			);
		});
	}
}
