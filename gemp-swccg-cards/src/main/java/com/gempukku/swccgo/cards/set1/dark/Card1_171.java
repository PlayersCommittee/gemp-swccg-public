package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.cards.evaluators.ForceIconsPresentEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Djas Puhr
 */
public class Card1_171 extends AbstractAlien {
    public Card1_171() {
        super(Side.DARK, 1, 4, 1, 4, 4, "Djas Puhr", Uniqueness.UNIQUE);
        setLore("Male Sakiyan, a race often employed as assassins. Bounty hunter. Incredible infrared peripheral vision. Has excellent aural and olfactory senses. Often tracks by scent.");
        setGameText("Power +1 for each [Dark Side Force] present, +2 under 'nighttime conditions.' Immune to attrition < 3.");
        addIcons(Icon.WARRIOR);
        addKeywords(Keyword.BOUNTY_HUNTER);
        setSpecies(Species.SAKIYAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Evaluator darkSideForceIconsPresent = new ForceIconsPresentEvaluator(self, true, false);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new ConditionEvaluator(darkSideForceIconsPresent,
                new MultiplyEvaluator(2, darkSideForceIconsPresent), new UnderNighttimeConditionConditions(self))));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
