package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.DuringBattleInitiatedByCondition;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Effect
 * Title: Battle Plan & Draw Their Fire
 */
public class Card12_041 extends AbstractNormalEffect {
    public Card12_041() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Battle Plan & Draw Their Fire", Uniqueness.UNIQUE);
        addComboCardTitles(Title.Battle_Plan, Title.Draw_Their_Fire);
        setGameText("Deploy on table. You may initiate battles for free. During a battle you initiate, each time opponent plays an interrupt, opponent must first use 1 Force. For either player to initiate a Force drain, that player must first use 3 Force unless that player occupies a battleground site and a battleground system. (Immune to Alter.)");
        addIcons(Icon.CORUSCANT);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, player));
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.and(Filters.opponents(self), Filters.Interrupt),
                        new DuringBattleInitiatedByCondition(player), 1));
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(player, Filters.battleground_site),
                        new OccupiesCondition(player, Filters.battleground_system))), 3, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(opponent, Filters.battleground_site),
                new OccupiesCondition(opponent, Filters.battleground_system))), 3, opponent));
        return modifiers;
    }
}