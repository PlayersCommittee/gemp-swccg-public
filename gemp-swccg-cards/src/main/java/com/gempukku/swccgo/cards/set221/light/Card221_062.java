package com.gempukku.swccgo.cards.set221.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 21
 * Type: Location
 * Subtype: System
 * Title: Geonosis
 */
public class Card221_062 extends AbstractSystem {
    public Card221_062() {
        super(Side.LIGHT, Title.Geonosis, 7, ExpansionSet.SET_21, Rarity.V);
        setLocationDarkSideGameText("Unless your Fett (or [Separatist] starship) here or at a related asteroid sector, Force drain -1 here.");
        setLocationLightSideGameText("Your [Clone Army] starships are power +1 here. Your starships at the nearest related asteroid sector may move here as a 'react.'");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CLONE_ARMY, Icon.EPISODE_I, Icon.PLANET, Icon.VIRTUAL_SET_21);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter fettOrSeparatistStarshipFilter = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Filters.Fett, Filters.and(Icon.SEPARATIST, Filters.starship)));
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new UnlessCondition(new AtCondition(self, fettOrSeparatistStarshipFilter, Filters.or(Filters.here(self), Filters.relatedAsteroidSector(self)))), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Icon.CLONE_ARMY, Filters.starship, Filters.here(self)), 1));
        modifiers.add(new MayMoveOtherCardsAsReactToLocationModifier(self, "Move starship as a react", playerOnLightSideOfLocation, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship, Filters.at(Filters.nearestRelatedAsteroidSector(self))), Filters.any));
        return modifiers;
    }
}