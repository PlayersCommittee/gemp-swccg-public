package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.InitiateBattlesForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Effect
 * Title: Battle Plan
 */
public class Card8_035 extends AbstractNormalEffect {
    public Card8_035() {
        super(Side.LIGHT, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Battle_Plan, Uniqueness.UNIQUE);
        setLore("Even though the landing of the stolen shuttle was successful, the Rebel strike team on Endor was forced to rethink their plans when Leia disappeared.");
        setGameText("Deploy on table. You may initiate battles for free. Also, for either player to initiate a Force drain, that player must first use 3 Force unless that player occupies a battleground site (except a holosite) and a battleground system. (Immune to Alter.)");
        addIcons(Icon.ENDOR);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateBattlesForFreeModifier(self, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(player, Filters.battleground_site),
                new OccupiesCondition(player, Filters.battleground_system))), 3, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(opponent, Filters.battleground_site),
                new OccupiesCondition(opponent, Filters.battleground_system))), 3, opponent));
        return modifiers;
    }
}