package com.gempukku.swccgo.cards.set205.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.HereEvaluator;
import com.gempukku.swccgo.cards.evaluators.MaxEvaluator;
import com.gempukku.swccgo.cards.evaluators.StackedEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 5
 * Type: Character
 * Subtype: Imperial
 * Title: Sim Aloo (V)
 */
public class Card205_015 extends AbstractImperial {
    public Card205_015() {
        super(Side.DARK, 1, 3, 3, 4, 6, "Sim Aloo", Uniqueness.UNIQUE, ExpansionSet.SET_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial council member. Senior political advisor to Emperor Palpatine. Never speaks. Like his master, shows remarkable patience and wisdom.");
        setGameText("If Emperor on table, destiny +2 when drawn for destiny. While at Death Star II: Throne Room, your total battle destiny during battles anywhere is +X, where X = number of Dark Jedi here or cards stacked on Insignificant Rebellion.");
        addIcons(Icon.DEATH_STAR_II, Icon.VIRTUAL_SET_5);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyWhenDrawnForDestinyModifier(self, new OnTableCondition(self, Filters.Emperor), 2));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalBattleDestinyModifier(self, new AtCondition(self, Filters.Throne_Room),
                new MaxEvaluator(new HereEvaluator(self, Filters.Dark_Jedi), new StackedEvaluator(self, Filters.Insignificant_Rebellion)), playerId, true));
        return modifiers;
    }
}
