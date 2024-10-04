package com.gempukku.swccgo.cards.set304.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.ControlsWithCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.common.Keyword;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: The Great Hutt Expansion
 * Type: Location
 * Subtype: Site
 * Title: Claudius's Palace: Ice Lounge
 */
public class Card304_084 extends AbstractSite {
    public Card304_084() {
        super(Side.LIGHT, Title.Claudius_Ice_Lounge, Title.Koudooine, Uniqueness.UNIQUE, ExpansionSet.GREAT_HUTT_EXPANSION, Rarity.V);
        setLocationDarkSideGameText("If you control, Force drain +1 here and your Scholae Palatinae members deploy -2 here.");
        setLocationLightSideGameText("Your aliens deploy -1 here. If you control with a Tiure clan member, Force drain +1 here.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SCOMP_LINK, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.CLAUDIUS_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.alien), -1, self));
        modifiers.add(new ForceDrainModifier(self, new ControlsWithCondition(playerOnLightSideOfLocation, self, Filters.Clan_Tiure),
                1, playerOnLightSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Condition youControl = new ControlsCondition(playerOnDarkSideOfLocation, self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, youControl, 1, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.CSP),
                youControl, -2, self, true));
        return modifiers;
    }
}