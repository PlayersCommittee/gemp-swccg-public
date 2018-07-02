package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MoveCostToLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: Site
 * Title: Naboo: Theed Palace Throne Room
 */
public class Card12_083 extends AbstractSite {
    public Card12_083() {
        super(Side.LIGHT, Title.Theed_Palace_Throne_Room, Title.Naboo);
        setLocationLightSideGameText("While you occupy, opponent's [Presence] droids require +1 Force to move to here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
        addKeywords(Keyword.THEED_PALACE_SITE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostToLocationModifier(self, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Icon.PRESENCE, Filters.droid),
                new OccupiesCondition(playerOnLightSideOfLocation, self), 1, self));
        return modifiers;
    }
}