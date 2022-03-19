package com.keyspammer;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

import java.util.Locale;
import java.util.Random;

// shoutouts to OP from ly

@Extension
@PluginDescriptor(
	name = "Keypress Spammer",
	enabledByDefault = false,
	description = "Spams a message upon a manually set keypress, and calls pile spams.",
	tags = {"spam", "key", "keypress", "pile"}
)
public class SpammerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;

	@Inject
	private SpammerConfig config;

	@Inject
	private KeyManager keyManager;

	private String oldChat;
	private String opponentName;
	private boolean autoSpam;
	private int autoSpamDelay;
	private String[] prefixes;

	@Provides
	SpammerConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SpammerConfig.class);
	}

	private final HotkeyListener messageOneHotkeyListener = new HotkeyListener(() -> config.msgOneKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			String messageOne = config.messageOne();
			if (!messageOne.equals(""))
			{
				sendMessage(messageOne);
			}
		}
	};

	private final HotkeyListener messageTwoHotkeyListener = new HotkeyListener(() -> config.msgTwoKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			String messageTwo = config.messageTwo();
			if (!messageTwo.equals(""))
			{
				sendMessage(messageTwo);
			}
		}
	};

	private final HotkeyListener messageThreeHotkeyListener = new HotkeyListener(() -> config.msgThreeKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			String messageThree = config.messageThree();
			if (!messageThree.equals(""))
			{
				sendMessage(messageThree);
			}
		}
	};

	private final HotkeyListener pilehotkeyListener = new HotkeyListener(() -> config.pilekeybind())
	{
		@Override
		public void hotkeyPressed() {
			pileSpam();
		}
	};

	private final HotkeyListener autoPileSpamHotkeyListener = new HotkeyListener(() -> config.autoPileSpamKeybind())
	{
		@Override
		public void hotkeyPressed()
		{
			autoSpam = !autoSpam;
		}
	};

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(messageOneHotkeyListener);
		keyManager.registerKeyListener(messageTwoHotkeyListener);
		keyManager.registerKeyListener(messageThreeHotkeyListener);

		keyManager.registerKeyListener(pilehotkeyListener);

		keyManager.registerKeyListener(autoPileSpamHotkeyListener);

		oldChat = "";
		opponentName = "";

		autoSpam = false;
		autoSpamDelay = 0;

		updatePrefixes();
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(messageOneHotkeyListener);
		keyManager.unregisterKeyListener(messageTwoHotkeyListener);
		keyManager.unregisterKeyListener(messageThreeHotkeyListener);

		keyManager.unregisterKeyListener(pilehotkeyListener);

		keyManager.unregisterKeyListener(autoPileSpamHotkeyListener);

		oldChat = "";
		opponentName = "";

		autoSpam = false;
		autoSpamDelay = 0;

		updatePrefixes();
	}

	private void pileSpam() {
		if (!opponentName.equals("")) {
			if (config.smallerPileNames()) {
				sendMessage(config.clanPrefix() + " " + smallerPileName(opponentName));
			} else {
				sendMessage(config.clanPrefix() + " " + opponentName);
			}
		}
	}

	@Subscribe
	private void onInteractingChanged(InteractingChanged event)
	{
		final Actor player = client.getLocalPlayer();
		if (player == null) { return; }

		final Actor source = event.getSource();
		final Actor target = event.getTarget();
		if (source == null) { return; }

		// Event is not from the Player
		if (source != player) { return; }

		// Player interacting with nothing -> Reset Target name
		if (target == null) { opponentName = ""; return; }

		if (opponentName == null) { opponentName = ""; return; }

		if (config.checkTargetIsPlayer() && !(target instanceof Player)) { // the target is not a player, return
			opponentName = "";
			return;
		}

		opponentName = target.getName();

		if (config.ignorePrefix()) {
			for (String p : prefixes) {
				if (opponentName.length() <= p.length()) { continue; }
				if (opponentName.substring(0, p.length()).equalsIgnoreCase(p)) {
					opponentName = opponentName.substring(p.length());
					break;
				}
			}
		}

	}

	private String smallerPileName(String name) {
		boolean ignoreSpaces = Math.random() > 0.5; // occasionally, ignore spaces in a players name to seem more humanlike
		if (ignoreSpaces) {
			name = name.replace(" ", "");
		}

		name = name.toLowerCase(Locale.ROOT);

		int nameSize = random(3, 6);
		if (name.length() < nameSize) {
			nameSize = name.length();
		}

		return name.substring(0, nameSize);
	}

	private int random(int min, int max) {
		return (int) ((Math.random() * (max - min + 1)) + min);
	}

	@Subscribe
	private void onVarClientStrChanged(VarClientStrChanged varClient)
	{
		String newChat = client.getVar(VarClientStr.CHATBOX_TYPED_TEXT);
		if (varClient.getIndex() == VarClientStr.CHATBOX_TYPED_TEXT.getIndex() && !newChat.equals(oldChat))
		{
			oldChat = newChat;
		}
	}

	@Subscribe
	private void onGameTick(GameTick event) {
		autoSpamDelay--;
		if (autoSpamDelay < 0) { autoSpamDelay = 0; }

		if (autoSpam && autoSpamDelay <= 0) {
			pileSpam();
			autoSpamDelay += config.autoPileSpamDelay();
		}
	}

	private void sendMessage(String text)
	{
		clientThread.invoke(() -> {
			String cached = oldChat;
			client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, text);
			client.runScript(5517, text); // 5517 = new pub chat script
			oldChat = cached;
			client.setVar(VarClientStr.CHATBOX_TYPED_TEXT, oldChat);
		});
	}

	@Subscribe
	private void onConfigChanged(ConfigChanged event) {
		if (!event.getGroup().equals("keyspammer")) { return; }

		updatePrefixes();
	}

	private void updatePrefixes() { prefixes = config.ignoredPrefixes().trim().split("\\s*,\\s*"); }
}
