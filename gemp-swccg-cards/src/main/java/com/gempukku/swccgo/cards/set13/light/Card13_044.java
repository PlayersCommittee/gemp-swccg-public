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
 * Title: Ultimatum
 */
public class Card13_044 extends AbstractDefensiveShield {
    public Card13_044() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ultimatum, ExpansionSet.REFLECTIONS_III, Rarity.PM);
        setLore("'Jabba! This is your last chance. Free us or die.'");
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