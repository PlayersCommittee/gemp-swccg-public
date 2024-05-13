package com.gempukku.swccgo.cards.set13.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
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
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.InitiateForceDrainCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Battle Plan
 */
public class Card13_008 extends AbstractDefensiveShield {
    public Card13_008() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Battle_Plan, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Even though the landing of the stolen shuttle was successful, the Rebel strike team on Endor was forced to rethink their plans when Leia disappeared.");
        setGameText("Plays on table. For either player to initiate a Force drain, that player must first use 3 Force unless that player occupies a battleground site and a battleground system.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String player = self.getOwner();
        String opponent = game.getOpponent(player);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(player, Filters.battleground_site),
                new OccupiesCondition(player, Filters.battleground_system))), 3, player));
        modifiers.add(new InitiateForceDrainCostModifier(self, new UnlessCondition(new AndCondition(new OccupiesCondition(opponent, Filters.battleground_site),
                new OccupiesCondition(opponent, Filters.battleground_system))), 3, opponent));
        return modifiers;
    }
}