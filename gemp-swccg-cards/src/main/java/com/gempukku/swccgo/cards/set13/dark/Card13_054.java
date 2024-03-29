package com.gempukku.swccgo.cards.set13.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Battle Order
 */
public class Card13_054 extends AbstractDefensiveShield {
    public Card13_054() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Battle_Order, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Administration of the Imperial installation on Endor includes coordination of troops on the ground and tight security provided by the Empire's space fleet.");
        setGameText("Plays on table. Unless Battle Plan on table, for either player to initiate a Force drain, that player must first use 3 Force unless that player occupies a battleground site and a battleground system.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);
        Condition battlePlanOnTable = new OnTableCondition(self, Filters.Battle_Plan);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateForceDrainCostModifier(self, Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)), new UnlessCondition(new OrCondition(battlePlanOnTable,
                new AndCondition(new OccupiesCondition(player, Filters.battleground_site), new OccupiesCondition(player, Filters.battleground_system)))), 3, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, Filters.not(Filters.immuneToCardTitle(Title.Battle_Order)), new UnlessCondition(new OrCondition(battlePlanOnTable,
                new AndCondition(new OccupiesCondition(opponent, Filters.battleground_site), new OccupiesCondition(opponent, Filters.battleground_system)))), 3, opponent));
        return modifiers;
    }
}