package net.runelite.client.plugins.cwa;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Inject;

class CwaTimer extends Timer
{
    @Inject
    private Notifier notifier;

    @Inject
    private CwaConfig config;

    @Getter
    @Setter
    private boolean visible;

    CwaTimer(CwaPlugin plugin, ItemManager itemManager)
    {
        super(122, ChronoUnit.SECONDS, itemManager.getImage(ItemID.KODAI_WAND), plugin);
        setTooltip("Time Left until the round starts");
    }

    @Override
    public Color getTextColor()
    {
        Duration timeLeft = Duration.between(Instant.now(), getEndTime());
        if ( timeLeft.getSeconds() <= 20)
        {
            return Color.RED.brighter();
        }
        return Color.WHITE;
    }
}
