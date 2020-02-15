package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Premiere
 * Type: Character
 * Subtype: Droid
 * Title: C-3PO (See-Threepio)
 */
public class Card1_005 extends AbstractDroid {
    public Card1_005() {
        super(Side.LIGHT, 3, 3, 1, 4, "C-3PO (See-Threepio)", Uniqueness.UNIQUE);
        setLore("Cybot Galactica 3PO human-cyborg relations droid. Fluent in over six million forms of communication. 112 years old. Has never been memory-wiped... as far as he knows.");
        setGameText("Total power at same site is +2 for each of your droid/Rebel pairs present, including C-3PO. R2-D2 is forfeit +2 when present.");
        addPersona(Persona.C3PO);
        addModelType(ModelType.PROTOCOL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalPowerModifier(self, Filters.sameSite(self),
                new MultiplyEvaluator(2, new MinEvaluator(new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.droid)),
                        new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.Rebel)))), self.getOwner()));
        modifiers.add(new ForfeitModifier(self, Filters.R2D2, new PresentCondition(self, Filters.R2D2), 2));
        return modifiers;
    }
}
