package com.wautotyper;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.VarClientStr;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "[w] Auto Typer",
	enabledByDefault = false,
	description = "Auto types custom message(s)",
	tags = {"spam", "key", "keypress"}
)
public class wAutoTyperPlugin extends Plugin
{
	@Inject
	private Client client;

	@Getter(AccessLevel.PACKAGE)
	@Inject
	private ClientThread clientThread;

	@Inject
	private wAutoTyperConfig config;

	@Inject
	private KeyManager keyManager;

	private String[] messages;
	private int delay;
	private int messageIndex;
	private boolean enabled;
	private String oldChat;

	@Provides
	wAutoTyperConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(wAutoTyperConfig.class);
	}

	private final HotkeyListener toggleHotKeyListener = new HotkeyListener(() -> config.toggleKeyBind())
	{
		@Override
		public void hotkeyPressed()
		{
			toggle();
		}
	};

	private void toggle() {
		this.enabled = !this.enabled;
		this.messages = this.parseMessages(config.messages());
		this.delay = 0;
		this.messageIndex = 0;
		this.oldChat = "";
	}

	private String[] parseMessages(String messages) {
		return messages.split("\\r?\\n");
	}

	@Override
	protected void startUp()
	{
		keyManager.registerKeyListener(toggleHotKeyListener);
		this.delay = 0;
		this.messageIndex = 0;
		this.enabled = false;
		this.messages = null;
		this.oldChat = "";
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(toggleHotKeyListener);
		this.delay = 0;
		this.messageIndex = 0;
		this.enabled = false;
		this.messages = null;
		this.oldChat = "";
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
		if (!this.enabled) { return; }
		this.delay--;
		if (this.delay < 0) { this.delay = 0; }
		if (this.delay != 0) { return; }

		String message = this.nextMessage();
		if (message.equals("")) { return; }

		this.sendMessage(message);
		this.oldChat = message;
		this.delay += config.delay();

	}

	private String nextMessage() {
		if (this.messageIndex >= this.messages.length) {
			this.messageIndex = 0;
		}
		if (messages.length == 0) { return ""; }
		String message = messages[messageIndex];
		messageIndex++;
		return message;
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
}