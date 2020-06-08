package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.UnderNighttimeConditionConditions;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character
 * Subtype: Alien
 * Title: Melas
 */
public class Card7_032 extends AbstractAlien {
    public Card7_032() {
        super(Side.LIGHT, 2, 3, 2, 4, 4, "Melas", Uniqueness.UNIQUE);
        setLore("Sarkan smuggler. Smokes an Essoomian gruu pipe to heighten awareness. Exiled from his home planet of Sarka for displaying curiosity in other aliens. Misses his homeworld.");
        setGameText("Adds 2 to power of anything he pilots. Power +2 under 'nighttime conditions.' Immune to attrition < X, where X = number of your aliens present (including himself).");
        addIcons(Icon.SPECIAL_EDITION, Icon.PILOT);
        setSpecies(Species.SARKAN);
        addKeywords(Keyword.SMUGGLER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new PowerModifier(self, new UnderNighttimeConditionConditions(self), 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new PresentEvaluator(self, Filters.and(Filters.your(self), Filters.alien))));
        return modifiers;
    }
}
