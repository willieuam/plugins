package com.wuav;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class wUAVRunningInfoBox extends InfoBox
{
    private final wUAVPlugin plugin;

    wUAVRunningInfoBox(final BufferedImage image, final wUAVPlugin plugin)
    {
        super(image, plugin);
        this.plugin = plugin;
        setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText()
    {
        return plugin.getPlayers() == null ? "-1" : String.valueOf(plugin.getPlayers().size());
    }

    @Override
    public Color getTextColor()
    {
        return Color.WHITE;
    }

    @Override
    public boolean render()
    {
        return plugin.isRunning();
    }

    @Override
    public boolean cull()
    {
        return false;
    }
}
