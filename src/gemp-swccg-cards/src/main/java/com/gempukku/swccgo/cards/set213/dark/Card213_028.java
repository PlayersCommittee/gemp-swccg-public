package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Malachor: Sith Temple Upper Chamber
 */
public class Card213_028 extends AbstractSite {
    public Card213_028() {
        super(Side.DARK, "Malachor: Sith Temple Upper Chamber", Title.Malachor, Uniqueness.UNIQUE, ExpansionSet.SET_13, Rarity.V);
        setLocationDarkSideGameText("Force drain +1 here. While Vader alone here, he is power +2.");
        setLocationLightSideGameText("While Ahsoka alone (or with Ezra) here, she is power +2.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_13);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, self, 1, playerOnDarkSideOfLocation));
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Vader, Filters.alone, Filters.here(self)), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.Ahsoka, Filters.or(Filters.alone, Filters.with(self, Filters.Ezra)), Filters.here(self)), 2));
        return modifiers;
    }
}