package com.gempukku.swccgo.cards.set204.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LandsFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TakesOffFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: Site
 * Title: Jakku: Niima Outpost Shipyard
 */
public class Card204_027 extends AbstractSite {
    public Card204_027() {
        super(Side.LIGHT, Title.Niima_Outpost_Shipyard, Title.Jakku, Uniqueness.UNIQUE, ExpansionSet.SET_4, Rarity.V);
        setLocationDarkSideGameText("Your gangsters and scavengers deploy -1 here.");
        setLocationLightSideGameText("Your starfighters take off and land here for free.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.EPISODE_VII, Icon.EXTERIOR_SITE, Icon.PLANET, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation),
                Filters.or(Filters.gangster, Filters.scavenger)), -1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarfighters = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.starfighter, Filters.deploysAndMovesLikeStarfighter));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TakesOffFreeFromLocationModifier(self, yourStarfighters, self));
        modifiers.add(new LandsFreeToLocationModifier(self, yourStarfighters, self));
        return modifiers;
    }
}