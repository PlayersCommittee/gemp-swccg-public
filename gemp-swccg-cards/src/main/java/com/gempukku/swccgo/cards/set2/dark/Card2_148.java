package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: System
 * Title: Ralltiir
 */
public class Card2_148 extends AbstractSystem {
    public Card2_148() {
        super(Side.DARK, Title.Ralltiir, 3);
        setLocationDarkSideGameText("Devastator is power +1 here and may move to or from here for free.");
        setLocationLightSideGameText("Tantive IV is power +1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Devastator, Filters.here(self)), 1));
        modifiers.add(new MovesFreeToLocationModifier(self, Filters.Devastator, self));
        modifiers.add(new MovesFreeFromLocationModifier(self, Filters.Devastator, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Tantive_IV, Filters.here(self)), 1));
        return modifiers;
    }
}