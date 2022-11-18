package com.gempukku.swccgo.cards.set204.dark;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotDrawMoreThanBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesFreeFromLocationModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 4
 * Type: Location
 * Subtype: System
 * Title: Jakku
 */
public class Card204_051 extends AbstractSystem {
    public Card204_051() {
        super(Side.DARK, Title.Jakku, 4, ExpansionSet.SET_4, Rarity.V);
        setLocationDarkSideGameText("Your [First Order] starships move from here for free.");
        setLocationLightSideGameText("Unless your Resistance character or [Resistance] starship here, you may not draw more than one battle destiny here.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.EPISODE_VII, Icon.VIRTUAL_SET_4);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MovesFreeFromLocationModifier(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Icon.FIRST_ORDER, Filters.starship), self));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, self, new UnlessCondition(new HereCondition(self,
                Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.or(Filters.Resistance_character, Filters.and(Icon.RESISTANCE, Filters.starship))))),
                1, playerOnLightSideOfLocation));
        return modifiers;
    }
}