package net.runelite.client.plugins.cwa;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

@Singleton
public class DragonScimOverlay extends Overlay {

    private final Client client;
    private final CwaPlugin plugin;



    @Inject
    public DragonScimOverlay(final Client client, final CwaPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        setPriority(OverlayPriority.HIGH);
        setPosition(OverlayPosition.DYNAMIC);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Rectangle rectangle = new Rectangle();
        rectangle.setBounds(client.getCanvas().getBounds());
        rectangle.setLocation(client.getCanvas().getLocation());
        graphics.setStroke(new BasicStroke(3));
        graphics.setColor(Color.RED);
        graphics.draw(rectangle);
        return null;
    }
}