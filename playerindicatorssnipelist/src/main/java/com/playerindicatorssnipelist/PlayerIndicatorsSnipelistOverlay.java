/*
 * Copyright (c) 2018, Tomas Slusny <slusnucky@gmail.com>
 * Copyright (c) 2019, Jordan Atwood <nightfirecat@protonmail.com>
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
 *
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
package com.playerindicatorssnipelist;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.ItemID;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.api.WorldType;
import net.runelite.api.kit.KitType;
import net.runelite.client.game.ChatIconManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class PlayerIndicatorsSnipelistOverlay extends Overlay
{
	private static final int ACTOR_OVERHEAD_TEXT_MARGIN = 40;
	private static final int ACTOR_HORIZONTAL_TEXT_MARGIN = 10;

	private final BufferedImage skullIcon = ImageUtil.getResourceStreamFromClass(PlayerIndicatorsSnipelistPlugin.class,
		"skull.png");

	private final PlayerIndicatorsSnipelistPlugin plugin;
	private final PlayerIndicatorsSnipelistConfig config;
	private final PlayerIndicatorsSnipelistService playerIndicatorsExtendedService;

	@Inject
	private Client client;

	@Inject
	private ChatIconManager chatIconManager;

	@Inject
	public PlayerIndicatorsSnipelistOverlay(PlayerIndicatorsSnipelistPlugin plugin, PlayerIndicatorsSnipelistConfig config, PlayerIndicatorsSnipelistService playerIndicatorsExtendedService)
	{
		this.plugin = plugin;
		this.config = config;
		this.playerIndicatorsExtendedService = playerIndicatorsExtendedService;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(OverlayPriority.MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		playerIndicatorsExtendedService.forEachPlayer((player, playerRelation) -> drawSceneOverlays(graphics, player, playerRelation));
		return null;
	}

	private void drawSceneOverlays(Graphics2D graphics, Player actor, PlayerIndicatorsSnipelistPlugin.PlayerRelation relation)
	{
		if (actor.getName() == null || !plugin.getLocationHashMap().containsKey(relation))
		{
			return;
		}

		final List indicationLocations = Arrays.asList(plugin.getLocationHashMap().get(relation));
		final Color color = plugin.getRelationColorHashMap().get(relation);
		final boolean skulls = config.playerSkull();
		final String name = actor.getName();
		final int zOffset = actor.getLogicalHeight() + ACTOR_OVERHEAD_TEXT_MARGIN;
		final Point textLocation = actor.getCanvasTextLocation(graphics, name, zOffset);

		if (indicationLocations.contains(PlayerIndicatorsSnipelistPlugin.PlayerIndicationLocation.ABOVE_HEAD))
		{
			final StringBuilder nameSb = new StringBuilder(name);

			if (config.showCombatLevel())
			{
				nameSb.append(" (");
				nameSb.append(actor.getCombatLevel());
				nameSb.append(")");
			}

			if (config.unchargedGlory() &&
				actor.getPlayerComposition().getEquipmentId(KitType.AMULET) == ItemID.AMULET_OF_GLORY)
			{
				nameSb.append(" (glory)");
			}

			final String builtString = nameSb.toString();
			final int x = graphics.getFontMetrics().stringWidth(builtString);
			final int y = graphics.getFontMetrics().getHeight();

			if (config.highlightClan() && actor.isFriendsChatMember() && config.showFriendsChatRanks() && relation == PlayerIndicatorsSnipelistPlugin.PlayerRelation.CLAN)
			{
				if (plugin.getRank(actor.getName()) != null)
				{
					final BufferedImage clanRankImage = chatIconManager.getRankImage(plugin.getRank(actor.getName()));
					if (clanRankImage != null)
					{
						renderActorTextAndImage(graphics, actor, builtString, color,
							ImageUtil.resizeImage(clanRankImage, y, y), 0, ACTOR_HORIZONTAL_TEXT_MARGIN);
					}
				}
			}
			else if (skulls && actor.getSkullIcon() != null && relation.equals(PlayerIndicatorsSnipelistPlugin.PlayerRelation.TARGET))
			{

				renderActorTextAndImage(graphics, actor, builtString, color,
					ImageUtil.resizeImage(skullIcon, y, y), ACTOR_OVERHEAD_TEXT_MARGIN, ACTOR_HORIZONTAL_TEXT_MARGIN);
			}
			else
			{
				renderActorTextOverlay(graphics, actor, builtString, color);
			}
		}
		if (actor.getConvexHull() != null && indicationLocations.contains(PlayerIndicatorsSnipelistPlugin.PlayerIndicationLocation.HULL))
		{
			OverlayUtil.renderPolygon(graphics, actor.getConvexHull(), color);
		}

		if (indicationLocations.contains(PlayerIndicatorsSnipelistPlugin.PlayerIndicationLocation.TILE))
		{
			if (actor.getCanvasTilePoly() != null)
			{
				OverlayUtil.renderPolygon(graphics, actor.getCanvasTilePoly(), color);
			}
		}
	}

	private boolean checkWildy()
	{
		return client.getVar(Varbits.IN_WILDERNESS) == 1 || WorldType.isAllPvpWorld(client.getWorldType());
	}

	public static void renderActorTextAndImage(Graphics2D graphics, Actor actor, String text, Color color, BufferedImage image, int yOffset, int xOffset)
	{
		Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + yOffset);

		if (textLocation != null)
		{
			OverlayUtil.renderImageLocation(graphics, textLocation, image);
			textLocation = new Point(textLocation.getX() + xOffset, textLocation.getY());
			OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
		}
	}

	public static void renderActorTextOverlay(Graphics2D graphics, Actor actor, String text, Color color)
	{
		renderActorTextOverlay(graphics, actor, text, color, 40);
	}

	public static void renderActorTextOverlay(Graphics2D graphics, Actor actor, String text, Color color, int offset)
	{
		Point textLocation = actor.getCanvasTextLocation(graphics, text, actor.getLogicalHeight() + offset);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, text, color);
		}
	}
}
