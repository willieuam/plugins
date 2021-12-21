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

package net.runelite.client.plugins.spammer;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ScriptID;
import net.runelite.api.VarClientStr;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ChatInput;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.opponentinfo.OpponentInfoPlugin;
import net.runelite.client.util.HotkeyListener;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "Keypress Spammer",
	enabledByDefault = false,
	description = "Spams a message upon a manually set keypress",
	tags = {"spam"}
)

// shoutouts to OP from ly

@Slf4j
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

	private String oldChat = "";
	private String opponentName = "";
	private boolean autoSpam = false;
	private int autoSpamDelay = 0;

	private static final int OVERHEAD_TEXT_DURATION = 4;

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
		public void hotkeyPressed()
		{
			if (!opponentName.equals(""))
			{
				sendMessage(config.clanPrefix() + " " + opponentName);
			}
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
		opponentName = target.getName();
		if (opponentName == null) { opponentName = ""; return; }

		//smallerNameSize = ;

		if (opponentName.length() > 4 && config.smallerPileNames()) {
			//hopefully will fix combined names being spammed
			if (opponentName.substring(0,4).toLowerCase().equals(target.getName().substring(0,4).toLowerCase()))
			{
				opponentName = opponentName.substring(0,4);
			}
		}
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

		//log.info("autoSpamDelay:" + autoSpamDelay);
		if (autoSpam && autoSpamDelay <= 0 && !opponentName.equals("")) {
			sendMessage(config.clanPrefix() + " " + opponentName);
			autoSpamDelay += OVERHEAD_TEXT_DURATION * 2;
		}
	}

	//@Subscribe
	//private void onChatMessage(ChatMessage event) {
	//	if (client.getLocalPlayer() == null) { return; }
	//
	//	if (autoSpam && event.getName().equals(client.getLocalPlayer().getName())) {
	//		autoSpamDelay += OVERHEAD_TEXT_DURATION * 2;
	//	}
	//}

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