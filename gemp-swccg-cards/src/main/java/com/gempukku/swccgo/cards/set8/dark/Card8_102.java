package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.InBattleAtCondition;
import com.gempukku.swccgo.cards.evaluators.MinEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToDrivenBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Endor
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Grond
 */
public class Card8_102 extends AbstractImperial {
    public Card8_102() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Grond, Uniqueness.UNIQUE);
        setLore("Colonel Dyer's aide. Coordinates scout actions. As a youth, raced swoops on homeworld of Corellia. Formerly served with Emperor's Demonstration Team.");
        setGameText("Adds 2 to power of anything he pilots or any swoop he drives. When in battle at an exterior site, adds 2 to attrition against opponent for each of your biker scout/trooper (non-biker scout) pairs present.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsPowerToDrivenBySelfModifier(self, 2, Filters.swoop));
        modifiers.add(new AttritionModifier(self, new InBattleAtCondition(self, Filters.exterior_site),
                        new MultiplyEvaluator(2,
                                new MinEvaluator(
                                        new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.biker_scout)),
                                        new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.trooper, Filters.not(Filters.biker_scout))))),
                        game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
