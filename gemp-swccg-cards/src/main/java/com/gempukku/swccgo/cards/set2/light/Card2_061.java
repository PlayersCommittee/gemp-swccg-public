package com.gempukku.swccgo.cards.set2.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: A New Hope
 * Type: Location
 * Subtype: System
 * Title: Corellia
 */
public class Card2_061 extends AbstractSystem {
    public Card2_061() {
        super(Side.LIGHT, Title.Corellia, 1);
        setLocationDarkSideGameText("Each of your starships are hyperspeed +1 when moving from here.");
        setLocationLightSideGameText("Your Falcon and your Corellian corvettes may deploy here as a 'react.'");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship), 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy Falcon or Corellian corvette as a 'react'",
                playerOnLightSideOfLocation, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.Falcon, Filters.Corellian_corvette)), self));
        return modifiers;
    }
}