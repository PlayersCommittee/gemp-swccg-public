package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Location
 * Subtype: System
 * Title: Rendezvous Point
 */
public class Card7_123 extends AbstractSystem {
    public Card7_123() {
        super(Side.LIGHT, Title.Rendezvous_Point, 13);
        setLocationLightSideGameText("Your starships deploy -2 here and are hyperspeed +2 when moving to or from here. Neither player may Force drain here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.SPECIAL_EDITION, Icon.SPACE);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarships = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, yourStarships, -2, self));
        modifiers.add(new HyperspeedWhenMovingToLocationModifier(self, yourStarships, 2, self));
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, yourStarships, 2, self));
        modifiers.add(new MayNotForceDrainAtLocationModifier(self));
        return modifiers;
    }
}