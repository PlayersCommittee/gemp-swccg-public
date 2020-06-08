package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.LostPodraceCondition;
import com.gempukku.swccgo.cards.conditions.WonPodraceCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: System
 * Title: Malastare
 */
public class Card12_077 extends AbstractSystem {
    public Card12_077() {
        super(Side.LIGHT, Title.Malastare, 3);
        setLocationDarkSideGameText("If you have lost a Podrace, you may not draw more than one battle destiny here.");
        setLocationLightSideGameText("If you have won a Podrace, Force drain +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, self, new LostPodraceCondition(playerOnDarkSideOfLocation),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new WonPodraceCondition(playerOnLightSideOfLocation), 1, playerOnLightSideOfLocation));
        return modifiers;
    }
}