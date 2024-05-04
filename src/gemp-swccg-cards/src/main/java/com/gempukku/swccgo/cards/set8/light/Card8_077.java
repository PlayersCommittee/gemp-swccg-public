package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Location
 * Subtype: Site
 * Title: Endor: Rebel Landing Site (Forest)
 */
public class Card8_077 extends AbstractSite {
    public Card8_077() {
        super(Side.LIGHT, Title.Rebel_Landing_Site, Title.Endor, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLocationDarkSideGameText("If you occupy, opponent's Rebel Landing Site game text is canceled.");
        setLocationLightSideGameText("Your starfighters land here for free. Your droids and [Endor] Rebels are deploy -1 here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.ENDOR, Icon.EXTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.FOREST);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.Rebel_Landing_Site,
                new OccupiesCondition(playerOnDarkSideOfLocation, self), game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LandsFreeToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.starfighter), self));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation),
                Filters.or(Filters.droid, Filters.and(Icon.ENDOR, Filters.Rebel))), -1, self));
        return modifiers;
    }
}