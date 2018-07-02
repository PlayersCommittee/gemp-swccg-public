package com.gempukku.swccgo.cards.set6.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.*;
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
 * Set: Jabba's Palace
 * Type: Effect
 * Title: Ultimatum
 */
public class Card6_058 extends AbstractNormalEffect {
    public Card6_058() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ultimatum, Uniqueness.UNIQUE);
        setLore("'Jabba! This is your last chance. Free us or die.'");
        setGameText("Deploy on your side of table. While you occupy at least three battlegrounds or opponent occupies no battlegrounds, you lose no more than 2 Force from each Force drain or 'insert' card. (Immune to Alter.)");
        addIcons(Icon.JABBAS_PALACE);
        addImmuneToCardTitle(Title.Alter);
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