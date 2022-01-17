package com.autocaster;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxPriority;

class AutoCasterEnabledInfoBox extends InfoBox
{
    AutoCasterPlugin plugin;

    AutoCasterEnabledInfoBox(final BufferedImage image, final AutoCasterPlugin plugin)
    {
        super(image, plugin);
        this.plugin = plugin;
        setPriority(InfoBoxPriority.HIGH);
    }

    @Override
    public String getText()
    {
        return this.plugin.isEnabled() ? "ON" : "OFF";
    }

    @Override
    public Color getTextColor()
    {
        return this.plugin.isEnabled() ? Color.GREEN : Color.RED;
    }

    @Override
    public boolean render()
    {
        return true;
    }

    @Override
    public boolean cull()
    {
        return false;
    }
}
