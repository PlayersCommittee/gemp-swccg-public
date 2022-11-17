package com.gempukku.swccgo.cards.set12.light;

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
import com.gempukku.swccgo.logic.modifiers.MoveCostFromLocationUsingHyperspeedModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Location
 * Subtype: System
 * Title: Tatooine
 */
public class Card12_084 extends AbstractSystem {
    public Card12_084() {
        super(Side.LIGHT, Title.Tatooine, 7, ExpansionSet.CORUSCANT, Rarity.U);
        setLocationDarkSideGameText("Unless your [Trade Federation] or [Independent Starship] starship here, you may not draw more than one battle destiny here.");
        setLocationLightSideGameText("Your movement from here using hyperspeed requires -1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDrawMoreThanBattleDestinyModifier(self, self, new UnlessCondition(new HereCondition(self,
                Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.or(Icon.TRADE_FEDERATION, Icon.INDEPENDENT), Filters.starship))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MoveCostFromLocationUsingHyperspeedModifier(self, Filters.your(playerOnLightSideOfLocation), -1, self));
        return modifiers;
    }
}