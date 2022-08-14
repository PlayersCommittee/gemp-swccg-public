package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 18
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Level 6 Core Shaft Corridor (V)
 */
public class Card218_017 extends AbstractSite {
    public Card218_017() {
        super(Side.LIGHT, "Death Star: Level 6 Core Shaft Corridor", Title.Death_Star);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("If you occupy, opponent's Level 6 Core Shaft Corridor game text is canceled.");
        setLocationLightSideGameText("Your stormtroopers here may move as a 'react' to a battle or Force drain at an adjacent site.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self, Filters.title("Death Star: Level 6 Core Shaft Corridor"),
                new OccupiesCondition(playerOnDarkSideOfLocation, self), game.getOpponent(playerOnDarkSideOfLocation)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move your stormtrooper as a react", playerOnLightSideOfLocation,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.stormtrooper, Filters.here(self)), Filters.adjacentSite(self)));
        return modifiers;
    }
}