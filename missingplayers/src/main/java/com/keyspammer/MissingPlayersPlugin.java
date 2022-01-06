package com.keyspammer;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Extension
@PluginDescriptor(
	name = "Missing Players",
	enabledByDefault = false,
	description = "Display missing players from CC",
	tags = {"clan", "pvp", "cc", "friends", "chat", "fc"}
)

@Slf4j
public class MissingPlayersPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MissingPlayersConfig config;

	@Inject
	private KeyManager keyManager;

	@Provides
	MissingPlayersConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(MissingPlayersConfig.class);
	}

	private final HotkeyListener missingPlayersKeyBindListener = new HotkeyListener(() -> config.missingPlayersKeyBind())
	{
		@Override
		public void hotkeyPressed()
		{
			String mps = missingPlayersString();
			StringSelection stringSelection = new StringSelection(mps);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		}
	};

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(missingPlayersKeyBindListener);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(missingPlayersKeyBindListener);
	}

	private String missingPlayersString() {
		Map<String, Integer> missingPlayers = missingPlayers();
		StringBuilder missingPlayersString= new StringBuilder();
		for (String mp : missingPlayers.keySet()) {
			missingPlayersString.append(mp);
			missingPlayersString.append(" (w");
			missingPlayersString.append(missingPlayers.get(mp));
			missingPlayersString.append(")\n");
		}
		return missingPlayersString.toString();
	}

	private Map<String, Integer> missingPlayers() {
		Map<String, Integer> missingPlayers = new HashMap<>();
		Map<String, Integer> fc = playersInFriendsChat();
		List<String> loc = playersAtLoc();

		for (String pfc : fc.keySet()) {
			if (!loc.contains(pfc)) {
				log.info("missing: " + pfc);
				missingPlayers.put(pfc, fc.get(pfc));
			}
		}

		return missingPlayers;
	}

	private Map<String, Integer> playersInFriendsChat() {
		HashMap<String, Integer> players = new HashMap<>();

		final FriendsChatManager friendsChatManager = client.getFriendsChatManager();

		if (friendsChatManager == null) { return players; }

		for (FriendsChatMember fcm : friendsChatManager.getMembers()) {
			log.info("fc: " + fcm.getName());
			players.put(fcm.getName(), fcm.getWorld());
		}
		return players;
	}

	private List<String> playersAtLoc() {
		List<String> players = new ArrayList<>();

		for (Player p : client.getPlayers()) {
			log.info("loc: " + p.getName());
			players.add(p.getName());
		}
		return players;
	}
}