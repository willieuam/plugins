package com.wuav;

import javax.inject.Inject;

import com.google.inject.Provides;
import com.openosrs.client.OpenOSRS;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Extension
@PluginDescriptor(
	name = "[w] UAV",
	enabledByDefault = false,
	description = "Log players in game and in CC",
	tags = {"clan", "pvp", "cc", "friends", "chat", "fc"}
)
public class wUAVPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private wUAVConfig config;

	@Inject
	private KeyManager keyManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Provides
	wUAVConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(wUAVConfig.class);
	}

	private final HotkeyListener ccPlayersKeyBindListener = new HotkeyListener(() -> config.ccPlayersKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			String ps = playerNameListString(playersInFriendsChat());
			writeLog(ps, "CC");
		}
	};

	private final HotkeyListener inGamePlayersKeyBindListener = new HotkeyListener(() -> config.inGamePlayersKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			if (running) {
				String ps = playerNameListString(players);
				writeLog(ps, "GAME");
				running = false;
			} else {
				players = playersAtLoc();
				running = true;
			}
		}
	};

	private final static String LOGSDIR = "uavlogs";
	private final static File logsFileDir;

	static {
		logsFileDir = new File(OpenOSRS.OPENOSRS_DIR, LOGSDIR);
		logsFileDir.mkdirs();
	}

	@Getter(AccessLevel.PACKAGE)
	private boolean running;
	@Getter(AccessLevel.PACKAGE)
	private List<String> players;

	private wUAVRunningInfoBox runningInfoBox;

	@Override
	protected void startUp()
	{
		runningInfoBox = new wUAVRunningInfoBox(itemManager.getImage(ItemID.HOURGLASS), this);
		infoBoxManager.addInfoBox(runningInfoBox);

		keyManager.registerKeyListener(ccPlayersKeyBindListener);
		keyManager.registerKeyListener(inGamePlayersKeyBindListener);

		running = false;
		players = null;
	}

	@Override
	protected void shutDown()
	{
		infoBoxManager.removeInfoBox(runningInfoBox);
		runningInfoBox = null;

		keyManager.unregisterKeyListener(ccPlayersKeyBindListener);
		keyManager.unregisterKeyListener(inGamePlayersKeyBindListener);

		running = false;
		players = null;
	}

	@Subscribe
	protected void onPlayerSpawned(PlayerSpawned event) {
		if (!running || event == null || event.getPlayer() == null) { return; }

		if (!players.contains(event.getPlayer().getName())) {
			players.add(event.getPlayer().getName());
		}
	}

	private List<String> playersInFriendsChat() {
		List<String> players = new ArrayList<>();

		final FriendsChatManager friendsChatManager = client.getFriendsChatManager();

		if (friendsChatManager == null) { return players; }

		for (FriendsChatMember fcm : friendsChatManager.getMembers()) {
			players.add(correctName(fcm.getName()));
		}
		return players;
	}

	private List<String> playersAtLoc() {
		List<String> players = new ArrayList<>();

		for (Player p : client.getPlayers()) {
			players.add(correctName(Objects.requireNonNull(p.getName())));
		}

		return players;
	}

	private void writeLog(String pl, String id) {
		LocalDateTime now = LocalDateTime.now();

		String fileName = String.format(
				id + "_%d_%d_%d_%d_%d.txt",
				now.getYear(), now.getMonth().getValue(), now.getHour(), now.getMinute(), now.getSecond()
		);

		File log = new File(logsFileDir, fileName);

		try {
			Writer writer = new FileWriter(log);
			writer.write(pl);
			writer.flush();
			writer.close();
		} catch (Exception ignored) {}

	}

	private String playerNameListString(List<String> pl) {
		StringBuilder s = new StringBuilder();
		for (String p : pl) {
			s.append(p);
			s.append("\n");
		}
		return s.toString();
	}

	private String correctName(String name) { // the fc player has the wrong char code for a space compared to the other name???
		byte[] bytes = name.getBytes(StandardCharsets.US_ASCII);
		for (int i = 0; i<bytes.length; i++) {
			if (bytes[i] == 63) {
				bytes[i] = 32;
			}
		}
		return new String(bytes);
	}
}