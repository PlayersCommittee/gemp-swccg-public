package com.gempukku.swccgo.cards.set213.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingFromLocationModifier;
import com.gempukku.swccgo.logic.modifiers.HyperspeedWhenMovingToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Location
 * Subtype: System
 * Title: Corellia (V)
 */
public class Card213_055 extends AbstractSystem {
    public Card213_055() {
        super(Side.LIGHT, Title.Corellia, 1, ExpansionSet.SET_13, Rarity.V);
        setLocationDarkSideGameText("If This Place Can Be A Little Rough on table, Force drain -1 here.");
        setLocationLightSideGameText("Your Corellians and freighters deploy -1 here. Your starships are hyperspeed +1 when moving to or from here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.A_NEW_HOPE, Icon.PLANET, Icon.VIRTUAL_SET_13);
        setVirtualSuffix(true);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, self, new OnTableCondition(self, Filters.This_Place_Can_Be_A_Little_Rough), -1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        Filter yourStarships = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.starship);
        Filter yourFreightersAndCorellians = Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.freighter, Filters.Corellian));

        modifiers.add(new HyperspeedWhenMovingFromLocationModifier(self, yourStarships, 1, self));
        modifiers.add(new HyperspeedWhenMovingToLocationModifier(self, yourStarships, 1, self));
        modifiers.add(new DeployCostToLocationModifier(self, yourFreightersAndCorellians, -1, self));
        return modifiers;
    }
}