package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleAtCondition;
import com.gempukku.swccgo.cards.conditions.DuringBattleInitiatedByCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Battle Order & First Strike
 */
public class Card12_129 extends AbstractNormalEffect {
    public Card12_129() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Battle Order & First Strike", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Battle_Order, Title.First_Strike);
        setGameText("Deploy on table. You may initiate battles for free. During a battle you initiate, each time opponent plays an interrupt, opponent must first use 1 Force. For either player to initiate a Force drain, that player must first use 3 Force unless Battle Plan on table, or that player occupies a battleground site and a battleground system. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);
        Condition battlePlanOnTable = new OnTableCondition(self, Filters.Battle_Plan);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattlesForFreeModifier(self, Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)), player));
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.opponents(self), Filters.Interrupt),
                new AndCondition(new DuringBattleInitiatedByCondition(player), new DuringBattleAtCondition(Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)))), 1));
        modifiers.add(new InitiateForceDrainCostModifier(self, Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)), new UnlessCondition(new OrCondition(battlePlanOnTable,
                new AndCondition(new OccupiesCondition(player, Filters.battleground_site), new OccupiesCondition(player, Filters.battleground_system)))), 3, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)), new UnlessCondition(new OrCondition(battlePlanOnTable,
                new AndCondition(new OccupiesCondition(opponent, Filters.battleground_site), new OccupiesCondition(opponent, Filters.battleground_system)))), 3, opponent));
        return modifiers;
    }
}