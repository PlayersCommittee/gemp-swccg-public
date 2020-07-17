package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifiersMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: Site
 * Title: Malachor: Sith Temple
 */
public class Card501_015 extends AbstractSite {
    public Card501_015() {
        super(Side.DARK, "Malachor: Sith Temple", Title.Malachor);
        setLocationDarkSideGameText("Your Force drain bonuses here may not be canceled. While alone, vader is power +2 here.");
        setLocationLightSideGameText("While alone (or with Ezra), Ahsoka is power +2 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_13);
        setTestingText("Malachor: Sith Temple");
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifiersMayNotBeCanceledModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.here(self))));
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