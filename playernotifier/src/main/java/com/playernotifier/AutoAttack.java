package com.playernotifier;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.SpriteID;
import net.runelite.api.widgets.WidgetInfo;

@AllArgsConstructor
@Getter
public enum AutoAttack {
    TB("Tele Block", 0, WidgetInfo.SPELL_TELE_BLOCK, SpriteID.SPELL_TELE_BLOCK, SpriteID.SPELL_TELE_BLOCK_DISABLED),
    BARRAGE("Ice Barrage", 1, WidgetInfo.SPELL_ICE_BARRAGE, SpriteID.SPELL_ICE_BARRAGE, SpriteID.SPELL_ICE_BARRAGE_DISABLED),
    BLITZ("Ice Blitz", 1, WidgetInfo.SPELL_ICE_BLITZ, SpriteID.SPELL_ICE_BLITZ, SpriteID.SPELL_ICE_BLITZ_DISABLED),
    ENTANGLE("Entangle", 0, WidgetInfo.SPELL_ENTANGLE, SpriteID.SPELL_ENTANGLE, SpriteID.SPELL_ENTANGLE_DISABLED),
    ATTACK("", -1, null, -1, -1);

    private final String name;
    private final int spellbook;
    private final WidgetInfo widgetInfo;
    private final int enabledSpriteId;
    private final int disabledSpriteId;

}
