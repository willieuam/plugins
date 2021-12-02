package net.runelite.client.plugins.cwa;

import lombok.Getter;
import lombok.Setter;
import net.runelite.api.ItemID;
import net.runelite.client.Notifier;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.Timer;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class DragonScimTimer extends Timer
{
    @Inject
    private Notifier notifier;

    @Inject
    private CwaConfig config;

    @Getter
    @Setter
    private boolean visible;

    DragonScimTimer(CwaPlugin plugin, ItemManager itemManager)
    {
        super(5, ChronoUnit.SECONDS, itemManager.getImage(ItemID.DRAGON_SCIMITAR), plugin);
        setTooltip("Time Left until you can use protection prayers");
    }

    @Override
    public Color getTextColor()
    {
        Duration timeLeft = Duration.between(Instant.now(), getEndTime());
        if ( timeLeft.getSeconds() <= 2)
        {
            return Color.green.brighter();
        }
        return Color.WHITE;
    }

    public long getTimeLeft()
    {
        Duration timeLeft = Duration.between(Instant.now(), getEndTime());

        return timeLeft.getSeconds();
    }

}
