package com.gempukku.swccgo.cards.set13.dark;

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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromInsertCardModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections III
 * Type: Defensive Shield
 * Title: Resistance
 */
public class Card13_084 extends AbstractDefensiveShield {
    public Card13_084() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Resistance, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("Oola had to choose between giving in to Jabba's constant advances or resisting him and inciting his wrath.");
        setGameText("Plays on table. While you occupy at least 3 battlegrounds or opponent occupies no battlegrounds, you lose no more than 2 Force from each Force drain or 'insert' card.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Condition condition = new OrCondition(new OccupiesCondition(playerId, 3, Filters.battleground),
                new NotCondition(new OccupiesCondition(opponent, Filters.battleground)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, condition, 2, playerId));
        modifiers.add(new LimitForceLossFromInsertCardModifier(self, condition, 2, playerId));
        return modifiers;
    }
}