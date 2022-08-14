package com.gempukku.swccgo.cards.set218.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
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
 * Set: Set 18
 * Type: Location
 * Subtype: System
 * Title: Polis Massa
 */
public class Card218_028 extends AbstractSystem {
    public Card218_028() {
        super(Side.LIGHT, Title.Polis_Massa, 8);
        setLocationDarkSideGameText("If a [Skywalker] Epic Event on table, Force drain -1 here. To move or deploy your starship to here requires +1 Force.");
        setLocationLightSideGameText("If your Skywalker here, opponent may not draw more than two battle destiny here.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.PLANET, Icon.EPISODE_I, Icon.VIRTUAL_SET_18);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        Filter yourStarships = Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.starship);
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new ForceDrainModifier(self, new OnTableCondition(self, Filters.and(Icon.SKYWALKER, Filters.Epic_Event)),-1, playerOnDarkSideOfLocation));
        modifiers.add(new DeployCostToLocationModifier(self, yourStarships, 1, self));
        modifiers.add(new MoveCostToLocationModifier(self, yourStarships, 1, self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, self, new HereCondition(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.Skywalker)), 2, game.getOpponent(playerOnLightSideOfLocation)));
        return modifiers;
    }
}