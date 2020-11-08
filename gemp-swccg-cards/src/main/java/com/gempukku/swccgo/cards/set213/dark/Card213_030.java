package com.gempukku.swccgo.cards.set213.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: System
 * Title: Wakeelmui (V)
 */
public class Card213_030 extends AbstractSystem {
    public Card213_030() {
        super(Side.DARK, Title.Wakeelmui, 2);
        setLocationDarkSideGameText("Your TIEs may deploy here as a 'react.'");
        setLocationLightSideGameText("While opponent has more starships here than you, your starships here are power -1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.PLANET, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Your TIEs may deploy here as a react.",
                playerOnDarkSideOfLocation, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.TIE), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();

        Filter wakeelmuiWithFewerLightSideShips = Filters.and(Filters.wherePlayerHasFewerStarships(self, playerOnLightSideOfLocation), Filters.sameLocation(self));
        Filter opponentsStarshipsHere = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship, Filters.at(wakeelmuiWithFewerLightSideShips));
        modifiers.add(new PowerModifier(self, opponentsStarshipsHere, -1));
        return modifiers;
    }
}