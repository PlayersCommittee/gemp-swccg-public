package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: System
 * Title: Kashyyyk
 */
public class Card2_146 extends AbstractSystem {
    public Card2_146() {
        super(Side.DARK, Title.Kashyyyk, 6, ExpansionSet.A_NEW_HOPE, Rarity.C1);
        setLocationDarkSideGameText("If you control, Force drain +1 here and all Wookiees on table are forfeit -3.");
        setLocationLightSideGameText("Your starships with any Wookiee aboard are power and forfeit +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnDarkSideOfLocation, self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, youControl, 1, playerOnDarkSideOfLocation));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.Wookiee, Filters.onTable), youControl, -3));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarshipsHereWithWookieeAboard = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship, Filters.here(self),
                Filters.hasAboard(self, Filters.Wookiee));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, yourStarshipsHereWithWookieeAboard, 2));
        modifiers.add(new ForfeitModifier(self, yourStarshipsHereWithWookieeAboard, 2));
        return modifiers;
    }
}