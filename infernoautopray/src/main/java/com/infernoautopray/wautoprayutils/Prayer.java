package com.infernoautopray.wautoprayutils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.widgets.WidgetInfo;

@AllArgsConstructor
@Getter
public enum Prayer {
    MAGIC("<col=ff9040>Protect from Magic</col>", WidgetInfo.PRAYER_PROTECT_FROM_MAGIC),
    RANGED("<col=ff9040>Protect from Missiles</col>", WidgetInfo.PRAYER_PROTECT_FROM_MISSILES),
    MELEE("<col=ff9040>Protect from Melee</col>", WidgetInfo.PRAYER_PROTECT_FROM_MELEE),
    NONE("", null);

    private final String name;
    private final WidgetInfo widget;
}