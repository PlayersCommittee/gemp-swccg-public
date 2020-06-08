package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Theed Palace Generator
 */
public class Card13_076 extends AbstractSite {
    public Card13_076() {
        super(Side.DARK, Title.Theed_Palace_Generator, Title.Naboo);
        setLocationDarkSideGameText("Your Dark Jedi are power +2 here.");
        setLocationLightSideGameText("Your Jedi are power +1 here. Force drain -1 here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.THEED_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Dark_Jedi, Filters.here(self)), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Jedi, Filters.here(self)), 1));
        modifiers.add(new ForceDrainModifier(self, -1, playerOnLightSideOfLocation));
        return modifiers;
    }
}