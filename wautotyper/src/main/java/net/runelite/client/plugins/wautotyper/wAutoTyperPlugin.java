/*
 * Copyright (c) 2019, St0newall
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package net.runelite.client.plugins.wautotyper;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
	name = "Auto Typer",
	enabledByDefault = false,
	description = "Auto types custom message(s)",
	tags = {"spam", "key", "keypress"}
)

@Slf4j
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

	private static final int OVERHEAD_TEXT_DURATION = 4;

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
		this.delay += OVERHEAD_TEXT_DURATION;

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